package com.example.muaring.common.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    // ⚪ 400
    REQUEST_HEADER_EMPTY(40001, HttpStatus.BAD_REQUEST, "요청 헤더가 비어있습니다."),
    DUPLICATE_USER_ID(40002, HttpStatus.BAD_REQUEST, "이미 사용 중인 아이디입니다."),
    NOT_VALID_EXCEPTION(40003, HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    INVALID_TEST_CONTENT(40004, HttpStatus.BAD_REQUEST, "내용은 필수입니다."),

    // ⚪ 404
    NOT_FOUND_TEST(40400, HttpStatus.NOT_FOUND, "존재하지 않는 테스트 입니다."),
    NOT_FOUND_URL(40401, HttpStatus.NOT_FOUND, "존재하지 않는 URL 입니다."),

    // ⚪ 405
    METHOD_NOT_ALLOWED(40502, HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),

    // ⚪ 406
    HTTP_MEDIA_TYPE_NOT_ACCEPTABLE(40601, HttpStatus.NOT_ACCEPTABLE, "요청한 미디어 타입을 제공할 수 없습니다."),

    // ⚪ 500
    INTERNAL_SERVER_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR,  "서버 내부 오류가 발생했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
