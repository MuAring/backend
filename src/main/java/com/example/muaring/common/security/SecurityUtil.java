package com.example.muaring.common.security;

import com.example.muaring.domain.auth.exception.AuthException;
import com.example.muaring.domain.auth.exception.AuthErrorCode;
import com.example.muaring.domain.auth.security.MemberPrincipal;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

// ✨현재 로그인한 사용자 정보를 꺼낼 수 있도록 해주는 클래스
@Slf4j
@UtilityClass
public class SecurityUtil {

    // ⚪ SecurityContext에서 Authentication 객체 가져오기
    private Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
        }
        return authentication;
    }

    // ⚪ 로그인한 사용자의 memberId 반환
    public Long getMemberId() {
        Authentication auth = getAuthentication();

        Object principal = auth.getPrincipal();

        // principal이 MemberPrincipal인지 검증
        if (principal instanceof MemberPrincipal memberPrincipal) {
            return memberPrincipal.getMemberId();
        }

        throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
    }

    // ⚪ 로그인한 사용자의 email 반환
    public String getMemberEmail() {
        Authentication auth = getAuthentication();

        Object principal = auth.getPrincipal();
        if (principal instanceof MemberPrincipal memberPrincipal) {
            return memberPrincipal.getEmail();
        }

        throw new AuthException(AuthErrorCode.UNAUTHORIZED_ACCESS);
    }

    // ⚪ 로그인한 사용자의 access token 반환
    public String getMemberAccessToken() {
        Authentication auth = getAuthentication();
        if (auth.getCredentials() instanceof String token) {
            return token;
        }
        return null;
    }
}