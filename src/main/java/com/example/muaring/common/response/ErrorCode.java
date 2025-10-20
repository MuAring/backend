package com.example.muaring.common.response;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getStatus();
    int getCode();
    String getMessage();
}