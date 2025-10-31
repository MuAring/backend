package com.example.muaring.domain.auth.service;

import com.example.muaring.domain.auth.client.OAuthProviderClient;
import com.example.muaring.domain.auth.dto.response.*;
import com.example.muaring.domain.auth.entity.AuthProvider;
import com.example.muaring.domain.auth.exception.AuthErrorCode;
import com.example.muaring.domain.auth.exception.AuthException;
import com.example.muaring.domain.member.repository.OAuthAccountRepository;
import com.example.muaring.common.security.JwtTokenProvider;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.entity.OAuthAccount;
import com.example.muaring.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// ✨ 카카오 로그인의 "인가코드(code) -> 엑세스 토큰(access_token) -> 사용자 정보 -> JWT 발급" 을 담당하는 클래스
@Service
@RequiredArgsConstructor
public class AuthService {

    private final List<OAuthProviderClient> providerClients;
    private final OAuthAccountRepository oauthAccountRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider; // 로그인 완료 후 JWT 생성기

    @Transactional
    public LoginResponseDTO login(String code, AuthProvider authProvider) {
        /*
         * 등록된 클라이언트 목록을 확인하여 해당 소셜 로그인을 처리할 수 있는 클라이언트 객체를 가져오고,
         * 지원하지 않는 provider라면 예외 발생
         */
        OAuthProviderClient client = providerClients.stream()
                .filter(c -> c.getAuthProvider() == authProvider)
                .findFirst()
                .orElseThrow(() -> new AuthException(AuthErrorCode.PROVIDER_NOT_SUPPORTED));

        OAuthTokenResponseDTO tokenResponseDTO = client.getAccessToken(code);
        OAuthMemberInfoResponseDTO memberInfoResponseDTO = client.fetchMemberInfo(tokenResponseDTO.accessToken());

        OAuthAccount account = oauthAccountRepository
                .findByAuthProviderAndAuthProviderId(authProvider, memberInfoResponseDTO.providerId())
                .orElseGet(() -> createMemberAndAccount(memberInfoResponseDTO, authProvider));

        String accessToken = jwtTokenProvider.generateAccessToken(account.getMember().getId(), account.getMember().getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(account.getMember().getId(), account.getMember().getEmail());
        boolean hasNickname = account.getMember().getNickname() != null && !account.getMember().getNickname().isEmpty();

        return new LoginResponseDTO(accessToken, refreshToken, account.getMember().getId(), account.getMember().getEmail(), hasNickname);
    }

    private OAuthAccount createMemberAndAccount(OAuthMemberInfoResponseDTO memberInfoResponseDTO, AuthProvider authProvider) {
        Member member = memberRepository.findByEmail(memberInfoResponseDTO.email())
                .orElseGet(() -> memberRepository.save(Member.CreateOAuthMember(memberInfoResponseDTO.email())
                ));

        // oAuthAccount 정보 생성
        return oauthAccountRepository.save(OAuthAccount.createOAuthAccount(authProvider, memberInfoResponseDTO.providerId(), member));
    }
}