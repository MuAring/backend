package com.example.muaring.domain.music.exception;

import com.example.muaring.common.exception.GeneralException;
import com.example.muaring.common.response.ErrorCode;
import lombok.*;

@Getter
public class MusicException extends GeneralException {

    public MusicException(ErrorCode errorCode) {
        super(errorCode);
    }
}
