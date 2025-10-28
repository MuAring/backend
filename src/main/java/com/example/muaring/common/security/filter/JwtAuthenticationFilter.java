package com.example.muaring.common.security.filter;

import com.example.muaring.common.security.JwtTokenProvider;
import com.example.muaring.domain.auth.security.MemberPrincipal;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/*
 * ✨ JWT 인증 필터
 * - 매 HTTP 요청마다 실행되며, Authorization 헤더의 JWT을 검증해 인증 정보를 SecurityContext에 등록한다.
 * */
@Component
@RequiredArgsConstructor
@Slf4j
/*
 * OncePerRequestFilter: Spring Security의 기본 Filter 구현체 중 하나로,
 * 같은 요청이 여러번 필터 체인을 통과하더라도 단 한번만 실행된다.
 * 따라서 JWT 검증 필터로 사용하기에 적합하다.
 * */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null) {
            try {
                // 토큰 검증 및 파싱
                if (jwtTokenProvider.validate(token)) {
                    Long memberId = jwtTokenProvider.getMemberId(token);
                    String email = jwtTokenProvider.getEmail(token);

                    // 인증 객체 생성
                    MemberPrincipal memberPrincipal = new MemberPrincipal(memberId, email);

                    /*
                    * UsernamePasswordAuthenticationToken: Spring Security에서 인증된 사용자를 표현하는 표준 클래스
                    * 3개의 파라미터
                    * 1. principal: 사용자 정보 (여기선 MemberPrincipal)
                    * 2. credentials: 인증 수단 (여기선 jwt 토큰)
                    * 3. authorities: 권한 목록 (지금은 빈 리스트)
                    * */
                    UsernamePasswordAuthenticationToken authentication
                            = new UsernamePasswordAuthenticationToken(memberPrincipal, token, List.of());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 모든 요청의 인증된 상태(authentication)를 SecurityContextHolder에 저장해두고, 이후 이 정보를 꺼내쓸 수 있음
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (JwtException e) {
                    log.warn("❌ JWT 검증 실패: {}", e.getMessage());
            }
        }

        // 모든 인증 절차를 마친 후 다음 필터로 요청을 넘긴다.
        filterChain.doFilter(request, response);
    }

    // ⚪HTTP 요청 헤더에서 JWT 문자열만 추출하는 메서드 (Bearer 7글자를 잘라내고 실제 토큰만 반환)
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}