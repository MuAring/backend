package com.example.muaring.domain.group.response;

import com.example.muaring.common.exception.GeneralException;
import com.example.muaring.common.response.ErrorCode;

public class GroupException extends GeneralException {
    public GroupException(ErrorCode errorCode) {
        super(errorCode);
    }
}
