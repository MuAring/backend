package com.example.muaring.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

// ✨ 모든 API 응답을 일관된 JSON 구조로 반환하기 위한 DTO
@JsonInclude(JsonInclude.Include.NON_NULL) // JSON 직렬화(객체 -> JSON) 과정에서 null을 가진 필드 제거
public record ApiResponse<T>(int code, String message, T data) {

    // ⚪ 성공 응답(데이터 + 메세지)
    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<T>(HttpStatus.OK.value(), message, data);
    }

    // ⚪ 성공 응답(데이터)
    public static <T> ApiResponse<T> ok(T data) {
        return ok(data, "요청이 성공했습니다.");
    }

    // ⚪ 성공 응답(메세지)
    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), message, null);
    }

    // ⚪ 생성 완료 응답(POST 201)
    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(HttpStatus.CREATED.value(), message, data);
    }

    // ⚪ 실패 응답
    public static <T> ApiResponse<T> fail(ErrorCode code, T data) {
        return new ApiResponse<>(code.getCode(), code.getMessage(), data);
    }
}