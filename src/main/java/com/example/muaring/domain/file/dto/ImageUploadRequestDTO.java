package com.example.muaring.domain.file.dto;

import com.example.muaring.domain.file.entity.ImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ImageUploadRequestDTO(
        @NotBlank(message = "파일 이름은 필수입니다.")
        String fileName,

        @NotBlank(message = "파일 타입은 필수입니다.")
        String fileType,

        @NotNull(message = "이미지 유형은 필수입니다.")
        ImageType imageType,

        @NotNull(message = "파일 크기는 필수입니다.")
        Long fileSize
) { }