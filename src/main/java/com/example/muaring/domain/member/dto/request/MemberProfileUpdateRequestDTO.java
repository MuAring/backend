package com.example.muaring.domain.member.dto.request;

import com.example.muaring.domain.file.dto.request.ImageCreateRequestDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record MemberProfileUpdateRequestDTO(
        @Size(min = 1, max = 10, message = "닉네임은 1자 이상, 10자 이하여야 합니다.")
        String nickname,
        @Valid
        ImageCreateRequestDTO imageRequestDTO,
        Boolean isPublic,
        Boolean isDiscoveryEnabled
) {
    // ⚪ 모든 필드가 null인지 검사하는 메서드
    @JsonIgnore
    @Schema(hidden = true)
    public boolean isAllFieldNull() {
        return nickname == null
                && imageRequestDTO == null
                && isPublic == null
                && isDiscoveryEnabled == null;
    }

    @JsonIgnore
    @Schema(hidden = true)
    public boolean isValidImageRequest() {
        return imageRequestDTO == null || imageRequestDTO.isCompleteImage();
    }
}