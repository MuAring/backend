package com.example.muaring.domain.auth.client;

import com.example.muaring.domain.auth.dto.response.*;
import com.example.muaring.domain.auth.entity.AuthProvider;
import com.example.muaring.domain.auth.exception.AuthErrorCode;
import com.example.muaring.domain.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class SpotifyOAuthClient implements OAuthProviderClient{

    private final WebClient webClient;

    // ⚪ application.yml의 설정값 불러오기
    @Value("${spotify.client-id}")
    private String clientId;

    @Value("${spotify.client-secret}")
    private String clientSecret;

    @Value("${spotify.redirect-uri}")
    private String redirectUri;

    @Value("${spotify.token-uri}")
    private String tokenUri;

    @Value("${spotify.user-info-uri}")
    private String userInfoUri;

    @Value("${spotify.scope}")
    private String scope;

    @Override
    public AuthProvider getAuthProvider() {
        return AuthProvider.SPOTIFY;
    }

    // ⚪ 인가 코드(code) -> 엑세스 토큰(access_token)
    @Override
    public OAuthTokenResponseDTO getAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);
        formData.add("scope", scope);

        SpotifyTokenResponseDTO res = webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new AuthException(AuthErrorCode.SOCIAL_TOKEN_EXCHANGE_FAILED)))
                .bodyToMono(SpotifyTokenResponseDTO.class)
                .block();

        if (res == null || res.spotifyAccessToken() == null) {
            throw new AuthException(AuthErrorCode.SOCIAL_TOKEN_EXCHANGE_FAILED);
        }

        return new OAuthTokenResponseDTO(
                res.spotifyAccessToken(),
                res.spotifyTokenType(),
                res.spotifyExpiresIn(),
                res.spotifyRefreshToken(),
                null
        );
    }

    @Override
    public OAuthMemberInfoResponseDTO fetchMemberInfo(String accessToken) {
        SpotifyMemberInfoResponseDTO res = webClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new AuthException(AuthErrorCode.SOCIAL_MEMBER_FETCH_FAILED)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new AuthException(AuthErrorCode.SOCIAL_MEMBER_FETCH_FAILED)))
                .bodyToMono(SpotifyMemberInfoResponseDTO.class)
                .block();

        if (res == null || res.spotifyProviderId() == null) {
            throw new AuthException(AuthErrorCode.SOCIAL_MEMBER_FETCH_FAILED);
        }

        return new OAuthMemberInfoResponseDTO(
                AuthProvider.SPOTIFY,
                String.valueOf(res.spotifyProviderId()),
                res.email()
        );
    }
}
