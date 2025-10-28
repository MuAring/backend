package com.example.muaring.config;

import com.example.muaring.common.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                /*
                 * CSRF(Cross-Site Request Forgery) 보호 기능 비활성화
                 * JWT 기반 인증에서는 세션이 없으므로 CSRF 토큰이 필요없기 때문이다. (CSRF는 "서버 세션 + 쿠키" 기반 인증에서만 의미가 있다.)
                 */
                .csrf(AbstractHttpConfigurer::disable)

                // 세션 정책을 STATELESS로
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 요청 URL 별 인가(Authorization) 정책
                .authorizeHttpRequests(auth -> auth
                        // 로그인, Swagger 문서는 인증없이 접근 허용 (이외의 요청은 JWT 인증 필요)
                        .requestMatchers("/auth/**","/login/**", "/oauth2/**","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )

                /*
                 * UsernamePasswordAuthenticationFilter 전에 JWT 필터 삽입
                 * 사용자의 모든 요청은 먼저 JwtAuthenticationFilter를 거친 뒤,
                 * 유효한 토큰이면 SecurityContext에 인증 정보을 넣고 Controller에서 Authentication으로 현재 사용자 접근 가능
                 */
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}