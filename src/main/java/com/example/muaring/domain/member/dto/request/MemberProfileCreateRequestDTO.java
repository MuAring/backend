package com.example.muaring.domain.member.dto.request;

import com.example.muaring.domain.file.entity.ImageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberProfileCreateRequestDTO(
        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다.")
        String nickname,
        String fileName,
        String fileType,
        ImageType imageType,
        String s3Key,
        Long fileSize
) { }