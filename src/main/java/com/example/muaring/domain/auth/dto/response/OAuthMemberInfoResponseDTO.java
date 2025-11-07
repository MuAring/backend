package com.example.muaring.domain.auth.dto.response;

import com.example.muaring.domain.auth.entity.AuthProvider;

public record OAuthMemberInfoResponseDTO(
        AuthProvider provider,
        String providerId, // 카카오: Long, 스포티파이: String
        String email
) {}