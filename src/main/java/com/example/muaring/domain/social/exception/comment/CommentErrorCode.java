package com.example.muaring.domain.social.exception.comment;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommentErrorCode implements ErrorCode {

    // 400
    CANNOT_REPLY_TO_DELETED_COMMENT(6201, HttpStatus.BAD_REQUEST, "삭제된 댓글에는 답글을 작성할 수 없습니다."),

    // 404
    COMMENT_NOT_FOUND(6202, HttpStatus.NOT_FOUND, "댓글이 존재하지 않습니다."),

    // 409
    COMMENT_ALREADY_DELETED(6203, HttpStatus.CONFLICT, "이미 삭제된 댓글입니다."),
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}