package com.example.muaring.domain.social.exception.comment;

import com.example.muaring.common.exception.GeneralException;

public class CommentException extends GeneralException {
    public CommentException(CommentErrorCode errorCode) {
        super(errorCode);
    }
}