package com.example.muaring.domain.auth.service;

import com.example.muaring.common.util.PkceUtil;
import com.example.muaring.domain.auth.client.OAuthProviderClient;
import com.example.muaring.domain.auth.client.PkceManager;
import com.example.muaring.domain.auth.client.SpotifyOAuthClient;
import com.example.muaring.domain.auth.dto.request.KakaoLoginRequest;
import com.example.muaring.domain.auth.dto.request.SpotifyLoginRequest;
import com.example.muaring.domain.auth.dto.response.*;
import com.example.muaring.domain.auth.entity.AuthProvider;
import com.example.muaring.domain.auth.exception.AuthErrorCode;
import com.example.muaring.domain.auth.exception.AuthException;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.OAuthAccountRepository;
import com.example.muaring.common.security.JwtTokenProvider;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.entity.OAuthAccount;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

// ✨ 소셜 로그인의 "인가코드(code) -> 엑세스 토큰(access_token) -> 사용자 정보 -> JWT 발급" 을 담당하는 클래스
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final List<OAuthProviderClient> providerClients;
    private final OAuthAccountRepository oauthAccountRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider; // 로그인 완료 후 JWT 생성기
    private final SpotifyOAuthClient spotifyOAuthClient;
    private final PkceManager pkceManager;

    @Value("${spotify.client-id}")
    private String spotifyClientId;

    @Value("${spotify.redirect-uri}")
    private String spotifyRedirectUri;

    @Transactional
    public LoginResponseDTO spotifyLogin(SpotifyLoginRequest request) {
        OAuthProviderClient client = findClient("SPOTIFY");
        OAuthTokenResponseDTO tokenResponseDTO = client.getAccessToken(request.code());
        OAuthMemberInfoResponseDTO memberInfoResponseDTO = client.fetchMemberInfo(tokenResponseDTO.accessToken());

        OAuthAccount account = findOrCreateAccount(memberInfoResponseDTO, "SPOTIFY");
        Member member = account.getMember();

        if (tokenResponseDTO.refreshToken() != null) {
            account.updateSpotifyRefreshToken(tokenResponseDTO.refreshToken());
        }

        String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId(), member.getEmail());
        boolean hasNickname = member.getNickname() != null && !member.getNickname().isEmpty();

        log.info("accessToken: " + accessToken);

        return LoginResponseDTO.of(
                member,
                accessToken,
                refreshToken,
                hasNickname,
                tokenResponseDTO.accessToken());
    }

    @Transactional
    public LoginResponseDTO kakaoLogin(KakaoLoginRequest request) {
        OAuthProviderClient client = findClient("KAKAO");

        OAuthMemberInfoResponseDTO memberInfoResponseDTO = client.fetchMemberInfo(request.kakaoAccessToken());
        OAuthAccount account = findOrCreateAccount(memberInfoResponseDTO, "KAKAO");
        Member member = account.getMember();
        String accessToken = jwtTokenProvider.generateAccessToken(member.getId(), member.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId(), member.getEmail());
        boolean hasNickname = member.getNickname() != null && !member.getNickname().isEmpty();

        log.info("accessToken: " + accessToken);

        return LoginResponseDTO.of(member, accessToken, refreshToken, hasNickname, null);
    }

    private OAuthAccount createMemberAndAccount(OAuthMemberInfoResponseDTO memberInfoResponseDTO, AuthProvider authProvider) {
        Member member = memberRepository.findByEmail(memberInfoResponseDTO.email())
                .orElseGet(() -> memberRepository.save(Member.CreateOAuthMember(memberInfoResponseDTO.email())
                ));

        // oAuthAccount 정보 생성
        return oauthAccountRepository.save(OAuthAccount.createOAuthAccount(authProvider, memberInfoResponseDTO.providerId(), member));
    }

    // ⚪ provider에 맞는 구현체를 찾아주는 메서드
    private OAuthProviderClient findClient(String provider) {
        AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
        return providerClients.stream()
                .filter(c -> c.getAuthProvider() == authProvider)
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthErrorCode.PROVIDER_NOT_SUPPORTED));
    }

    // ⚪ 가입된 계정이 없으면 계정을 생성하는 메서드
    private OAuthAccount findOrCreateAccount(OAuthMemberInfoResponseDTO memberInfoResponseDTO,
                                             String provider) {
        AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
        return oauthAccountRepository
                .findByAuthProviderAndAuthProviderId(authProvider, memberInfoResponseDTO.providerId())
                .orElseGet(() -> createMemberAndAccount(memberInfoResponseDTO, authProvider));
    }

    public AuthorizeUrlResponse generateAuthorizeUrl() {
        PkceUtil.Pkce pkce = PkceUtil.Pkce.generatePkce();
        String url = UriComponentsBuilder
                .fromUriString("https://accounts.spotify.com/authorize")
                .queryParam("client_id", spotifyClientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", spotifyRedirectUri)
                .queryParam("code_challenge", pkce.codeChallenge())
                .queryParam("code_challenge_method", "S256")
                .queryParam("scope", "user-read-email user-read-private")
                .build()
                .toUriString();

        // Pkce code_verifier 저장 → PKCE 검증에 필요
        log.info("codeVerifier={}", pkce.codeVerifier());

        pkceManager.saveCodeVerifier(pkce.codeVerifier());

        return AuthorizeUrlResponse.create(url);
    }

    @Transactional
    public SpotifyTokenRefreshResponseDTO refreshSpotifyAccessToken(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        OAuthAccount account = oauthAccountRepository
                .findByMemberIdAndAuthProvider(memberId, AuthProvider.valueOf("SPOTIFY"))
                .orElseThrow(() -> new AuthException(AuthErrorCode.OAUTH_ACCOUNT_NOT_FOUND));

        String refreshToken = account.getSpotifyRefreshToken();
        if (refreshToken == null) {
            throw new AuthException(AuthErrorCode.SPOTIFY_REFRESH_TOKEN_NOT_FOUND);
        }

        OAuthTokenResponseDTO tokenResponseDTO = spotifyOAuthClient.refreshAccessToken(refreshToken);

        if (tokenResponseDTO.refreshToken() != null) {
            account.updateSpotifyRefreshToken(tokenResponseDTO.refreshToken());
        }

        return new SpotifyTokenRefreshResponseDTO(
                tokenResponseDTO.accessToken(),
                tokenResponseDTO.expiresIn()
        );
    }
}