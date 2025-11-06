package com.example.muaring.domain.social.service;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.music.dto.MusicRequestDTO;
import com.example.muaring.domain.music.dto.SpotifyTrackDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.response.SpotifyTrackDetailResponse;
import com.example.muaring.domain.music.service.SpotifyAuthService;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import com.example.muaring.domain.music.repository.MusicRepository;
import com.example.muaring.domain.music.response.SpotifySearchResponse;
import com.example.muaring.domain.social.entity.MusicPost;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicPostService {

    private final MusicRepository musicRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    private final MusicPostRepository musicPostRepository;
    private final SpotifyAuthService spotifyAuthService;
    private final WebClient webClient;

    public MusicPostService(
            MusicRepository musicRepository,
            GroupRepository groupRepository,
            MemberRepository memberRepository,
            MusicPostRepository musicPostRepository,
            @Qualifier("spotifyApiWebClient") WebClient webClient,
            SpotifyAuthService spotifyAuthService
    ) {
        this.musicRepository = musicRepository;
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.musicPostRepository = musicPostRepository;
        this.webClient = webClient;
        this.spotifyAuthService = spotifyAuthService;
    }

    public List<SpotifyTrackDTO> searchMusic(String query) {

        String token;
        SpotifySearchResponse response;

        try {
            token = spotifyAuthService.getAccessToken();
        } catch (Exception e) {
            throw new MusicException(MusicErrorCode.SPOTIFY_AUTH_FAILED);
        }

        try {
            response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/search")
                            .queryParam("q", query)
                            .queryParam("type", "track")
                            .queryParam("limit", 10)
                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(SpotifySearchResponse.class)
                    .block();
            if (response == null || response.getTracks() == null) {
                throw new MusicException(MusicErrorCode.SPOTIFY_NO_RESULTS);
            }

        }catch (WebClientResponseException.NotFound e) {
            throw new MusicException(MusicErrorCode.SPOTIFY_NO_RESULTS);
        } catch (Exception e) {
            throw new MusicException(MusicErrorCode.SPOTIFY_SEARCH_FAILED);
        }

        return response.getTracks().getItems().stream()
                .map(item -> SpotifyTrackDTO.builder()
                        .spotifyId(item.getId())
                        .name(item.getName())
                        .artistName(item.getArtists().get(0).getName())
                        .albumName(item.getAlbum().getName())
                        .albumImgUrl(item.getAlbum().getImages().isEmpty() ? null : item.getAlbum().getImages().get(0).getUrl())
                        .popularity(item.getPopularity())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public List<MusicHistoryDTO> getMusicHistoryByMember(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MusicException(MusicErrorCode.MEMBER_NOT_FOUND);
        }

        List<MusicPost> posts = musicPostRepository.findByMemberId(memberId);

        return posts.stream()
                .map(post -> MusicHistoryDTO.builder()
                        .musicId(post.getMusic().getId())
                        .title(post.getMusic().getName())
                        .artist(post.getMusic().getArtistName())
                        .albumImage(post.getMusic().getAlbumImgUrl())
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private MusicRequestDTO fetchSpotifyTrackDetail(String spotifyId) {

        String token = spotifyAuthService.getAccessToken();

        SpotifyTrackDetailResponse response = webClient.get()
                .uri("/v1/tracks/{spotifyId}", spotifyId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(SpotifyTrackDetailResponse.class)
                .block();

        if (response == null) {
            throw new MusicException(MusicErrorCode.SPOTIFY_NO_RESULTS);
        }

        LocalDateTime releaseDate = null;
        if (response.getAlbum().getReleaseDate() != null) {
            releaseDate = LocalDateTime.parse(response.getAlbum().getReleaseDate() + "T00:00:00");
        }

        return MusicRequestDTO.builder()
                .spotifyId(response.getId())
                .name(response.getName())
                .artistId(response.getArtists().get(0).getId())
                .artistName(response.getArtists().get(0).getName())
                .albumName(response.getAlbum().getName())
                .albumImgUrl(response.getAlbum().getImages().isEmpty() ? null : response.getAlbum().getImages().get(0).getUrl())
                .popularity(response.getPopularity())
                .durationMs(response.getDurationMs())
                .releaseDate(releaseDate)
                .build();
    }


    @Transactional
    public MusicPost createMusicPost(Long memberId, Long groupId, String spotifyId, String content) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MusicException(MusicErrorCode.MEMBER_NOT_FOUND));

        Group group = null;
        if (groupId != null) {
            group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new MusicException(MusicErrorCode.GROUP_NOT_FOUND));
        }

        Music music = musicRepository.findBySpotifyId(spotifyId)
                .orElseGet(() -> {

                    MusicRequestDTO track = fetchSpotifyTrackDetail(spotifyId);

                    Music newMusic = Music.builder()
                            .spotifyId(track.getSpotifyId())
                            .name(track.getName())
                            .artistId(track.getArtistId())
                            .artistName(track.getArtistName())
                            .albumName(track.getAlbumName())
                            .albumImgUrl(track.getAlbumImgUrl())
                            .popularity(track.getPopularity())
                            .durationMs(track.getDurationMs())
                            .releaseDate(track.getReleaseDate())
                            .createdAt(LocalDateTime.now())
                            .build();

                    return musicRepository.save(newMusic);
                });

        MusicPost post = MusicPost.builder()
                .member(member)
                .music(music)
                .group(group)
                .isProfile(true)
                .content(content)
                .likeCount(0)
                .commentCount(0)
                .build();

        return musicPostRepository.save(post);
    }
}