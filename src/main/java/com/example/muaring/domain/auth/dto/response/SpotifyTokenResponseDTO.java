package com.example.muaring.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

// ✨스포티파이 토큰 발급 응답 DTO (스포티파이 서버 -> 우리 서버)
public record SpotifyTokenResponseDTO(
        @JsonProperty("access_token")
        String accessToken, // 스포티파이에서 발급한 accessToken

        @JsonProperty("token_type")
        String tokenType,  // 인증방식 (ex.bearer)

        @JsonProperty("expires_in")
        Integer expiresIn,  // 해당 값은 optional 이므로 값이 빠질 수 있기에 int가 아닌 Integer 사용

        @JsonProperty("refresh_token")
        String refreshToken  // 스포티파이에서 발급한 refreshToken
) { }
