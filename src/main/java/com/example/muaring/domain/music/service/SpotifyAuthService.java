package com.example.muaring.domain.music.service;

import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class SpotifyAuthService {

    private final String clientId;
    private final String clientSecret;
    private final WebClient webClient;

    public SpotifyAuthService(@Qualifier("spotifyAuthWebClient") WebClient webClient,
                              @Value("${spotify.client-id}") String clientId,
                              @Value("${spotify.client-secret}") String clientSecret) {
        this.webClient = webClient;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
            if (clientId == null || clientId.isBlank() || clientSecret == null || clientSecret.isBlank()) {
                throw new IllegalStateException("spotify.client-id or spotify.client-secret is not configured");
            }
        String authHeader = Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

        SpotifyTokenResponse response = webClient.post()
                .uri("/api/token")
                .header("Authorization", "Basic " + authHeader)
                .body(BodyInserters.fromFormData("grant_type", "client_credentials"))
                .retrieve()
                .bodyToMono(SpotifyTokenResponse.class)
                .block();

        if (response == null || response.getAccessToken() == null) {
            throw new MusicException(MusicErrorCode.SPOTIFY_AUTH_FAILED);
        }

        return response.getAccessToken();
    }

    @Getter
    @NoArgsConstructor
    private static class SpotifyTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("expires_in")
        private int expiresIn;
    }
}

