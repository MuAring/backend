package com.example.muaring.domain.social.exception;

import com.example.muaring.common.exception.GeneralException;

public class PostException extends GeneralException {
    public PostException(PostErrorCode errorCode) {
        super(errorCode);
    }
}