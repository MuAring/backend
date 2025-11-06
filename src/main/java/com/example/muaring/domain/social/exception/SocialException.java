package com.example.muaring.domain.social.exception;

import com.example.muaring.common.exception.GeneralException;

public class SocialException extends GeneralException {
    public SocialException(SocialErrorCode errorCode) {
        super(errorCode);
    }
}