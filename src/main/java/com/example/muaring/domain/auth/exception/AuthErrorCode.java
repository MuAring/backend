package com.example.muaring.domain.auth.exception;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    // 400
    PROVIDER_NOT_SUPPORTED(1001, HttpStatus.BAD_REQUEST, "지원되지 않는 인증 제공자입니다."),

    // 401
    INVALID_TOKEN(1002, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(1003, HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    UNSUPPORTED_TOKEN(1004, HttpStatus.UNAUTHORIZED, "지원되지 않는 토큰 형식입니다."),
    MALFORMED_TOKEN(1005, HttpStatus.UNAUTHORIZED, "손상된 토큰입니다."),
    UNAUTHORIZED_ACCESS(1006, HttpStatus.UNAUTHORIZED, "로그인이 필요한 요청입니다."),

    // 500
    KAKAO_MEMBER_FETCH_FAILED(1007, HttpStatus.INTERNAL_SERVER_ERROR, "카카오 사용자 정보 조회에 실패했습니다."),
    INVALID_JWT_SECRET(1008, HttpStatus.INTERNAL_SERVER_ERROR, "JWT 시크릿 키가 유효하지 않거나 Base64로 인코딩되지 않았습니다."),
    INVALID_JWT_TTL(1009, HttpStatus.INTERNAL_SERVER_ERROR, "JWT TTL 설정값이 올바르지 않습니다."),

    // 502
    KAKAO_TOKEN_EXCHANGE_FAILED(1010, HttpStatus.BAD_GATEWAY, "카카오 엑세스 토큰 발급에 실패했습니다."),
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}