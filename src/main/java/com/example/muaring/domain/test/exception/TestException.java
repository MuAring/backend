package com.example.muaring.domain.test.exception;

import com.example.muaring.common.exception.GeneralException;
import com.example.muaring.common.response.ErrorCode;

public class TestException extends GeneralException {
    public TestException(ErrorCode errorCode) {
        super(errorCode);
    }
}