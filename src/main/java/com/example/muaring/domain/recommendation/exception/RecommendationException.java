package com.example.muaring.domain.recommendation.exception;

import com.example.muaring.common.exception.GeneralException;
import com.example.muaring.common.response.ErrorCode;

public class RecommendationException extends GeneralException {
    public RecommendationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
