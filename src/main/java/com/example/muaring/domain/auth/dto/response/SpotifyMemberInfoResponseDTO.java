package com.example.muaring.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyMemberInfoResponseDTO(
        @JsonProperty("id")
        String spotifyProviderId, // 스포티파이가 발급한 고유 ID

        @JsonProperty("email")
        String email
) {
}
