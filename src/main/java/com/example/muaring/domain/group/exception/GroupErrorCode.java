package com.example.muaring.domain.group.exception;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {

    // 400번 에러
    CATEGORY_SELECTION_REQUIRED(5001, HttpStatus.BAD_REQUEST, "그룹 카테고리를 1개 이상 선택해주세요."),
    CATEGORY_SELECTION_EXCEEDED(5002, HttpStatus.BAD_REQUEST, "그룹 카테고리는 3개 이하로 선택해야 합니다."),
    CATEGORY_MUST_BE_THREE(5007, HttpStatus.BAD_REQUEST, "카테고리는 정확히 3개를 선택해야 합니다."),
    MAX_MEMBERS_TOO_SMALL(5008, HttpStatus.BAD_REQUEST, "최대 인원은 현재 멤버 수보다 작을 수 없습니다."),
    CANNOT_TRANSFER_TO_SELF(5011, HttpStatus.BAD_REQUEST, "자기 자신에게 관리자 권한을 이양할 수 없습니다."),

    // 403 에러
    NOT_GROUP_ADMIN(5009, HttpStatus.FORBIDDEN, "그룹 관리자만 수정할 수 있습니다."),
    NOT_GROUP_MEMBER(5010, HttpStatus.FORBIDDEN, "그룹 멤버만 조회할 수 있습니다."),

    // 403 에러
    NULL_MEMBER(5013, HttpStatus.FORBIDDEN, "멤버를 찾을 수 없습니다."),

    // 404 에러
    GROUP_NOT_FOUND(5003, HttpStatus.NOT_FOUND, "해당 그룹을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(5004, HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    METRICS_NOT_FOUND(5005, HttpStatus.NOT_FOUND, "핵심 필드를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(5012, HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),

    // 409 에러
    METRICS_CONFLICT(5006, HttpStatus.CONFLICT, "핵심 필드가 존재할 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
