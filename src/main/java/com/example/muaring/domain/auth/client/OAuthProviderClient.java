package com.example.muaring.domain.auth.client;

import com.example.muaring.domain.auth.dto.response.OAuthMemberInfoResponseDTO;
import com.example.muaring.domain.auth.dto.response.OAuthTokenResponseDTO;
import com.example.muaring.domain.auth.entity.AuthProvider;

public interface OAuthProviderClient {

    AuthProvider getAuthProvider();
    OAuthTokenResponseDTO getAccessToken(String code);
    OAuthMemberInfoResponseDTO fetchMemberInfo(String accessToken);
}