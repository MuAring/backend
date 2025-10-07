package com.example.muaring.domain.test.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public record TestRequestDTO (
        @NotBlank(message = "내용은 필수입니다.")
        String content
){ }
