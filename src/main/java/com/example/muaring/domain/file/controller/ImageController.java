package com.example.muaring.domain.file.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.file.dto.request.GroupImageRequestDTO;
import com.example.muaring.domain.file.dto.request.ImageUploadRequestDTO;
import com.example.muaring.domain.file.dto.response.PresignedUrlResponseDTO;
import com.example.muaring.domain.file.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // ⚪ 파일 업로드용 presigned URL 발급 API
    @PostMapping("/upload-presigned-url")
    @Operation(summary = "파일 업로드용 presigned URL 발급", description = "파일 업로드용 presigned URL 발급입니다.")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDTO>> generateUploadPresignedUrl(
            @Valid @RequestBody ImageUploadRequestDTO request
    ) {
        PresignedUrlResponseDTO response = imageService.generatePresignedUploadUrl(request);
        return ResponseEntity.ok(
                ApiResponse.ok(response, "파일 업로드용 presigned URL이 성공적으로 생성되었습니다.")
        );
    }

    // ⚪ 파일 다운로드용 presigned URL 발급 API
    @PostMapping("/{imageId}/download-presigned-url")
    @Operation(summary = "파일 다운로드용 presigned URL 발급", description = "파일 다운로드용 presigned URL 발급입니다.")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDTO>> generateDownloadPresignedUrl(@PathVariable Long imageId) {
        PresignedUrlResponseDTO response = imageService.generateDownloadPresignedUrl(imageId);
        return ResponseEntity.ok(
                ApiResponse.ok(response, "파일 다운로드용 presigned URL이 성공적으로 조회되었습니다.")
        );
    }

    // [POST] /images/group-upload
    // 그룹 프로필 이미지 설정 API
    @PostMapping("/group-upload")
    @Operation(
            summary = "그룹 프로필 이미지 설정",
            description = "S3에 업로드된 이미지를 그룹 프로필 이미지로 설정합니다. " +
                    "먼저 /upload-presigned-url API로 URL을 발급받아 S3에 업로드한 후, " +
                    "이 API를 호출하여 DB에 저장하고 그룹과 연결합니다."
    )
    public ResponseEntity<ApiResponse<Void>> confirmGroupImageUpload(
            @Valid @RequestBody GroupImageRequestDTO request
    ) {
        imageService.confirmGroupImageUpload(request);
        return ResponseEntity.ok(
                ApiResponse.ok(null, "그룹 프로필 이미지가 성공적으로 설정되었습니다.")
        );
    }
}