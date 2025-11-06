package com.example.muaring.domain.member.response;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    // 400 에러
    ALREADY_HAS_PROFILE(2001, HttpStatus.BAD_REQUEST, "이미 프로필 정보가 존재하는 회원입니다."),

    // 404 에러
    MEMBER_NOT_FOUND(2002, HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    METRICS_NOT_FOUND(2003, HttpStatus.NOT_FOUND, "핵심 필드를 찾을 수 없습니다."),

    // 409 에러
    METRICS_CONFLICT(2004, HttpStatus.CONFLICT, "핵심 필드가 존재할 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
