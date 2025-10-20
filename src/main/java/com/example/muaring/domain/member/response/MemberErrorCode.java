package com.example.muaring.domain.member.response;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    // 404 에러
    MEMBER_NOT_FOUND(40404, HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다.");;

    private final int code;
    private final HttpStatus status;
    private final String message;
}
