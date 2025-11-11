package com.example.muaring.domain.social.exception.comment;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    // 404
    COMMENT_NOT_FOUND(6201, HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}