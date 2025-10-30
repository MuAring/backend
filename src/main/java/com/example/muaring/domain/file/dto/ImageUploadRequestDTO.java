package com.example.muaring.domain.file.dto;

import com.example.muaring.domain.file.entity.ImageType;
import com.example.muaring.domain.file.exception.FileErrorCode;
import com.example.muaring.domain.file.exception.FileException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

public record ImageUploadRequestDTO(
        @NotBlank(message = "파일 이름은 필수입니다.")
        String fileName,

        @NotBlank(message = "파일 타입은 필수입니다.")
        @Pattern(regexp = "^image/(jpeg|jpg|png)$", message = "지원하지 않는 이미지 형식입니다.")
        String fileType,  // 확장자 관련

        @NotNull(message = "이미지 유형은 필수입니다.")
        ImageType imageType,  // MEMBER, GROUP

        @NotNull(message = "파일 크기는 필수입니다.")
        @Positive(message = "파일 크기는 양수여야 합니다.")
        Long fileSize,

        @Nullable  // GROUP 이미지 수정시에만 필요
        Long targetId
) {
        public ImageUploadRequestDTO {
                if (imageType == ImageType.GROUP && targetId == null) {
                        throw new FileException(FileErrorCode.TARGET_ID_REQUIRED_FOR_GROUP);
                }
                if (imageType == ImageType.MEMBER && targetId != null) {
                        throw new FileException(FileErrorCode.TARGET_ID_NOT_ALLOWED_FOR_MEMBER);
                }
        }
}