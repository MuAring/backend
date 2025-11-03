package com.example.muaring.domain.member.dto.request;

import com.example.muaring.domain.file.entity.ImageType;
import jakarta.validation.constraints.NotBlank;

public record MemberProfileCreateRequestDTO(
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,
        String fileName,
        String fileType,
        ImageType imageType,
        String s3Key,
        Long fileSize
) { }