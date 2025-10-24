package com.example.muaring.domain.auth.dto.request;

import com.example.muaring.domain.auth.entity.AuthProvider;

public record SocialLoginRequestDTO(
        String code,
        AuthProvider authProvider
) {}