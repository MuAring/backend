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
    TARGET_ID_REQUIRED_FOR_GROUP(3005, HttpStatus.BAD_REQUEST, "그룹 이미지 업로드 시 targetId는 필수입니다."),
    TARGET_ID_NOT_ALLOWED_FOR_MEMBER(3006, HttpStatus.BAD_REQUEST, "개인 프로필 이미지 업로드 시 targetId는 지정할 수 없습니다."),
    MISSING_GROUP_ID(3007, HttpStatus.BAD_REQUEST, "그룹 아이디가 존재하지 누락되었습니다."),
    INVALID_IMAGE_REQUEST(3008, HttpStatus.BAD_REQUEST, "잘못된 이미지 요청입니다.(필드값 누락)"),

    // 403
    FORBIDDEN_GROUP_IMAGE_UPLOAD(3009, HttpStatus.FORBIDDEN, "그룹 관리자만 그룹 이미지를 업로드할 수 있습니다."),

    // 404
    IMAGE_NOT_FOUND(3010, HttpStatus.NOT_FOUND, "이미지가 존재하지 않습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}