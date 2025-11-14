package com.example.muaring.domain.file.dto.request;

import com.example.muaring.domain.file.entity.ImageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record ImageCreateRequestDTO(
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

        @NotBlank(message = "s3 key는 필수입니다.")
        String s3Key

) {
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isCompleteImage() {
        return fileName != null &&
                fileType != null &&
                imageType != null &&
                fileSize != null;
    }
}
