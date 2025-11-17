package com.example.muaring.domain.file.dto.request;

import com.example.muaring.domain.file.entity.ImageType;
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

        // GROUP 이미지 수정시에만 필요
        Long targetId
) {
        @AssertTrue(message = "GROUP 이미지 업로드시 targetId는 필수입니다.")
        public boolean isGroupTargetIdValid() {
                return imageType != ImageType.GROUP || targetId != null;
        }

        @AssertTrue(message = "MEMBER 이미지 업로드에는 targetId가 사용될 수 없습니다.")
        public boolean isMemberTargetIdValid() {
                return imageType != ImageType.MEMBER || targetId == null;
        }


}