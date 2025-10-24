package com.example.muaring.common.security;

import com.example.muaring.domain.auth.exception.AuthErrorCode;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import com.example.muaring.domain.auth.exception.AuthException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

// ✨JWT 생성 및 검증
@Component
public class JwtTokenProvider{

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.access-ttl-seconds}")
    private long accessTtlSec;

    @Value("${app.jwt.refresh-ttl-seconds}")
    private long refreshTtlSec;

    private SecretKey key;

    /*
     * ⚪ 비밀키를 생성하는 메서드
     * @PostConstruct를 이용하여,
     * 앱 시작 시 한번만 Key 객체를 만들어 캐싱해두고,
     * .signWith(key)할 때마다 매번 새 Key를 만드는 것이 아닌 한 번 만들어둔 걸 재사용한다.
     */
    @PostConstruct
    private void initKey() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ⚪ AccessToken을 생성하는 메서드
    public String generateAccessToken(Long memberId, String email) {
        return generateToken(memberId, email, accessTtlSec);
    }

    // ⚪ RefreshToken을 생성하는 메서드
    public String generateRefreshToken(Long memberId, String email) {
        return generateToken(memberId, email, refreshTtlSec);
    }

    // ⚪ token을 생성하는 메서드
    private String generateToken(Long memberId, String email, long ttlSeconds) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + ttlSeconds * 1000);
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("email", email)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /*
    * ⚪ JWT 파싱 및 검증하는 메서드
    * 서명 검증, 만료 시간 검사, 유효하지 않은 경우 예외 발생
    * */
    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)  // 서명 검증용 키 등록
                .build()
                .parseSignedClaims(token);  // 실제 파싱 및 검증 (만료 시간 검사 및 형식 검증)
    }

    /*
     * ⚪ parse()를 통해 토큰 유효성을 검사하는 메서드
     * 서명 위조, 만료 등 예외 발생 시 false 반환
     * */
    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new AuthException(AuthErrorCode.EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new AuthException(AuthErrorCode.MALFORMED_TOKEN);
        } catch (IllegalArgumentException | JwtException e) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }
    }

    // ⚪ 토큰에서 사용자 ID를 추출하는 메서드
    public Long getMemberId(String token) {
        Claims claims = parse(token).getPayload();
        return Long.parseLong(claims.getSubject());
    }

    // ⚪ 토큰에서 사용자 email를 추출하는 메서드
    public String getEmail(String token) {
        Claims claims = parse(token).getPayload();
        return claims.get("email").toString();
    }
}