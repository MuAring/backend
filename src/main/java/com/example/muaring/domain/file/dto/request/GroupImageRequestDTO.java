package com.example.muaring.domain.file.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "그룹 프로필 이미지 설정 요청 DTO")
public class GroupImageRequestDTO {

    @Schema(description = "그룹 ID", example = "1")
    private Long groupId;

    @Schema(description = "S3에 업로드된 파일의 키", example = "group/550e8400-e29b-41d4-a716-446655440000")
    private String s3Key;

    @Schema(description = "파일 이름", example = "profile.jpg")
    private String fileName;

    @Schema(description = "파일 타입", example = "image/jpeg")
    private String fileType;

    @Schema(description = "파일 크기 (바이트)", example = "2048576")
    private Long fileSize;
}