package com.example.muaring.domain.social.exception.post;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    // 403
    UNAUTHROIZED(6003, HttpStatus.UNAUTHORIZED, "권한이 존재하지 않습니다."),

    // 404
    POST_NOT_FOUND(6001, HttpStatus.NOT_FOUND, "게시물이 존재하지 않습니다."),

    //409
    ALREADY_POSTED_TODAY(6002, HttpStatus.CONFLICT, "이미 게시물을 작성하였습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}