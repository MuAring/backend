package com.example.muaring.domain.auth.client;

import com.example.muaring.domain.auth.dto.response.KakaoMemberInfoResponseDTO;
import com.example.muaring.domain.auth.dto.response.KakaoTokenResponseDTO;
import com.example.muaring.domain.auth.dto.response.OAuthMemberInfoResponseDTO;
import com.example.muaring.domain.auth.dto.response.OAuthTokenResponseDTO;
import com.example.muaring.domain.auth.entity.AuthProvider;
import com.example.muaring.domain.auth.exception.AuthErrorCode;
import com.example.muaring.domain.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KakaoOAuthClient implements OAuthProviderClient{

    private final WebClient webClient;

    // ⚪ application.yml의 설정값 불러오기
    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.token-uri}")
    private String tokenUri;

    @Value("${kakao.user-info-uri}")
    private String userInfoUri;

    @Override
    public AuthProvider getAuthProvider() {
        return AuthProvider.KAKAO;
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

        KakaoTokenResponseDTO res = webClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new AuthException(AuthErrorCode.SOCIAL_TOKEN_EXCHANGE_FAILED)))
                .bodyToMono(KakaoTokenResponseDTO.class)
                .block();

        if (res == null || res.accessToken() == null) {
            throw new AuthException(AuthErrorCode.SOCIAL_TOKEN_EXCHANGE_FAILED);
        }

        return new OAuthTokenResponseDTO(
                res.accessToken(),
                res.tokenType(),
                res.expiresIn(),
                res.refreshToken(),
                res.refreshTokenExpiresIn()
        );
    }

    @Override
    public OAuthMemberInfoResponseDTO fetchMemberInfo(String accessToken) {
        KakaoMemberInfoResponseDTO res = webClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new AuthException(AuthErrorCode.SOCIAL_MEMBER_FETCH_FAILED)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new AuthException(AuthErrorCode.SOCIAL_MEMBER_FETCH_FAILED)))
                .bodyToMono(KakaoMemberInfoResponseDTO.class)
                .block();

        if (res == null || res.kakaoProviderId() == null) {
            throw new AuthException(AuthErrorCode.SOCIAL_MEMBER_FETCH_FAILED);
        }

        return new OAuthMemberInfoResponseDTO(
                AuthProvider.KAKAO,
                String.valueOf(res.kakaoProviderId()),
                res.kakaoAccount() != null ? res.kakaoAccount().kakaoEmail() : null
        );
    }
}