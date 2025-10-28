package com.example.muaring.domain.file.exception;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {

    // 404
    IMAGE_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "이미지가 존재하지 않습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}