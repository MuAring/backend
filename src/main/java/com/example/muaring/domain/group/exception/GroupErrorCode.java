package com.example.muaring.domain.group.exception;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {

    // 400번 에러
    CATEGORY_SELECTION_REQUIRED(40005, HttpStatus.BAD_REQUEST, "그룹 카테고리를 1개 이상 선택해주세요."),
    CATEGORY_SELECTION_EXCEEDED(40006, HttpStatus.BAD_REQUEST, "그룹 카테고리는 3개 이하로 선택해야 합니다."),

    // 404 에러
    GROUP_NOT_FOUND(40402, HttpStatus.NOT_FOUND, "해당 그룹을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(40403, HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
