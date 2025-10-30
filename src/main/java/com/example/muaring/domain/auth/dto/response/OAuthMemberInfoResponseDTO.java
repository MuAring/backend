package com.example.muaring.domain.auth.dto.response;

public record OAuthMemberInfoResponseDTO(
        String providerId, // 카카오: Long, 스포티파이: String
        String email
) {}