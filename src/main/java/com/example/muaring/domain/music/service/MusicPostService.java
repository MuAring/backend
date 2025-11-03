package com.example.muaring.domain.music.service;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.music.dto.SpotifyTrackDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.repository.MusicPostRepository;
import com.example.muaring.domain.music.repository.MusicRepository;
import com.example.muaring.domain.music.response.SpotifySearchResponse;
import com.example.muaring.domain.social.entity.MusicPost;
import jakarta.transaction.Transactional;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
        String token = spotifyAuthService.getAccessToken();

        SpotifySearchResponse response = webClient.get()
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
            return List.of();
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

    @Transactional
    public MusicPost createMusicPost(Long memberId, Long groupId, Long musicId, String content) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MusicException(MusicErrorCode.MEMBER_NOT_FOUND));

        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new MusicException(MusicErrorCode.MUSIC_NOT_FOUND));

        Group group = null;
        if (groupId != null) {
            group = groupRepository.findById(groupId)
                    .orElseThrow(() -> new MusicException(MusicErrorCode.GROUP_NOT_FOUND));
        }

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