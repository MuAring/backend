package com.example.muaring.domain.file.exception;

import com.example.muaring.common.exception.GeneralException;
import com.example.muaring.common.response.ErrorCode;

public class FileException extends GeneralException {
    public FileException(FileErrorCode errorCode) {
        super(errorCode);
    }
}
