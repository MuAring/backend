package com.example.muaring.domain.music.service;

import com.example.muaring.domain.music.dto.MusicRequestDTO;
import com.example.muaring.domain.music.dto.SpotifyTrackDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.repository.MusicRepository;
import com.example.muaring.domain.music.response.SpotifySearchResponse;
import com.example.muaring.domain.music.response.SpotifyTrackDetailResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MusicService {

    private final MusicRepository musicRepository;
    private final SpotifyAuthService spotifyAuthService;
    private final WebClient webClient;

    public MusicService(
            MusicRepository musicRepository,
            SpotifyAuthService spotifyAuthService,
            @Qualifier("spotifyApiWebClient") WebClient webClient) {
        this.musicRepository = musicRepository;
        this.spotifyAuthService = spotifyAuthService;
        this.webClient = webClient;
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
                .previewUrl(response.getPreviewUrl())
                .build();
    }

    @Transactional
    public Music findOrCreateMusic(String spotifyId) {

        return musicRepository.findBySpotifyId(spotifyId)
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
                            .previewUrl(track.getPreviewUrl())
                            .build();

                    return musicRepository.save(newMusic);
                });
    }
}
