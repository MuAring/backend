package com.example.muaring.domain.auth.dto.response;

public record OAuthTokenResponseDTO(

        String accessToken,
        String tokenType,
        Integer expiresIn,
        String refreshToken,
        Integer refreshTokenExpiresIn
) {}