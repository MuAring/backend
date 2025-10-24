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
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoOAuthClient implements OAuthProviderClient{

    // 카카오 API 호출용 HTTP 클라이언트
    private final RestTemplate restTemplate;

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
        // /oauth/token 엔드포인트는 폼 형식(x-www-form-urlencoded)으로 받기 때문에 해당 형식으로 요청 본문을 전송하도록 지정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("redirect_uri", redirectUri);
        map.add("code", code);

        /*
         * map: grant_type, client_id, code, redirect_uri 등 폼 데이터(body)
         * headers: Content-Type: application/x-www-form-urlencoded
         * HttpEntity: map과 headers을 하나로 묶은 HTTP 요청 객체
         * */
        HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(map, headers);

        /*
         * 실제 카카오 서버(https://kauth.kakao.com/oauth/token)에 POST 요청
         * */
        ResponseEntity<KakaoTokenResponseDTO> res = restTemplate.postForEntity(
                tokenUri, req, KakaoTokenResponseDTO.class
        );

        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
            throw new AuthException(AuthErrorCode.KAKAO_TOKEN_EXCHANGE_FAILED);
        }
        return new OAuthTokenResponseDTO(
                res.getBody().kakaoAccessToken(),
                res.getBody().kakaoTokenType(),
                res.getBody().kakaoExpiresIn(),
                res.getBody().kakaoRefreshToken(),
                res.getBody().kakaoRefreshTokenExpiresIn()
        );
    }

    @Override
    public OAuthMemberInfoResponseDTO fetchMemberInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> req = new HttpEntity<>(headers);

        ResponseEntity<KakaoMemberInfoResponseDTO> res = restTemplate.exchange(
                userInfoUri, HttpMethod.GET, req, KakaoMemberInfoResponseDTO.class
        );

        if  (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
            throw new AuthException(AuthErrorCode.KAKAO_MEMBER_FETCH_FAILED);
        }

        KakaoMemberInfoResponseDTO kakaoMemberInfoResponseDTO = res.getBody();
        return new OAuthMemberInfoResponseDTO(
                String.valueOf(kakaoMemberInfoResponseDTO.kakaoId()),
                kakaoMemberInfoResponseDTO.kakaoAccount().kakaoEmail()
        );
    }
}