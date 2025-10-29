package com.example.muaring.domain.file.exception;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {

    // 400
    INVALID_FILE_TYPE(3001, HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    INVALID_FILE_SIZE(3002, HttpStatus.BAD_REQUEST, "파일 크기가 허용 범위를 초과했습니다."),
    INVALID_FILE_NAME_EMPTY(3003, HttpStatus.BAD_REQUEST, "파일명이 비어있습니다."),
    INVALID_FILE_NAME_TOO_LONG(3004, HttpStatus.BAD_REQUEST, "파일명이 너무 깁니다."),

    // 404
    IMAGE_NOT_FOUND(3005, HttpStatus.NOT_FOUND, "이미지가 존재하지 않습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}