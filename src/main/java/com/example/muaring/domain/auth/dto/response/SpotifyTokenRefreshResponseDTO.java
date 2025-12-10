package com.example.muaring.domain.auth.dto.response;

public record SpotifyTokenRefreshResponseDTO(
        String spotifyAccessToken,
        Integer expiresIn
) {
}
