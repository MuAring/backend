package com.example.muaring.domain.auth.exception;

import com.example.muaring.common.exception.GeneralException;

public class AuthException extends GeneralException {

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }
}