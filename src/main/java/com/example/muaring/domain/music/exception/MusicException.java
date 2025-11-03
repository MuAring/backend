package com.example.muaring.domain.music.exception;

import com.example.muaring.common.response.ErrorCode;
import lombok.*;

@Getter
public class MusicException extends RuntimeException {
    private final ErrorCode errorCode;

    public MusicException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
