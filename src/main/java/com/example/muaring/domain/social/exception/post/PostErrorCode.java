package com.example.muaring.domain.social.exception.post;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    // 404
    POST_NOT_FOUND(6001, HttpStatus.NOT_FOUND, "게시물이 존재하지 않습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}