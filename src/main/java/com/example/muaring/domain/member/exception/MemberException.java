package com.example.muaring.domain.member.exception;

import com.example.muaring.common.exception.GeneralException;
import com.example.muaring.common.response.ErrorCode;

public class MemberException extends GeneralException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
