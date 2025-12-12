package com.example.muaring.domain.recommendation.response;

import com.example.muaring.common.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RecommendationErrorCode implements ErrorCode {

    // 9000~9999번대
    RATE_LIMIT_EXCEEDED(9001,
            HttpStatus.TOO_MANY_REQUESTS,
            "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),
    ;
    
    private final int code;
    private final HttpStatus status;
    private final String message;
}
