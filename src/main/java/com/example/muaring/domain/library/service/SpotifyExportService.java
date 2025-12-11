package com.example.muaring.domain.library.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SpotifyExportService {

    private final WebClient webClient = WebClient.builder().build();

    private final String SPOTIFY_API_BASE = "https://api.spotify.com/v1";

    public void exportTracks(String accessToken, List<String> trackIds) {

        String playlistId = createPlaylist(accessToken, "Muaring");

        addTracksToPlaylist(accessToken, playlistId, trackIds);
    }

    // 플레이리스트 생성
    private String createPlaylist(String accessToken, String playlistName) {

        Map<String, Object> body = Map.of(
                "name", playlistName,
                "description", "Exported from Muaring",
                "public", false
        );

        Map response = webClient.post()
                .uri(SPOTIFY_API_BASE + "/me/playlists")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return response.get("id").toString(); // playlistId
    }

    // 트랙 추가
    private void addTracksToPlaylist(String accessToken, String playlistId, List<String> trackIds) {

        List<String> uris = trackIds.stream()
                .map(id -> "spotify:track:" + id)
                .toList();

        Map<String, Object> body = Map.of("uris", uris);

        webClient.post()
                .uri(SPOTIFY_API_BASE + "/playlists/" + playlistId + "/tracks")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

}