package com.example.muaring.domain.file.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.file.dto.ImageUploadRequestDTO;
import com.example.muaring.domain.file.dto.PresignedUrlResponseDTO;
import com.example.muaring.domain.file.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // 파일 업로드용 presigned URL 발급 API
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

    // 파일 다운로드용 presigned URL 발급 API
    @PostMapping("/{imageId}/download-presigned-url")
    @Operation(summary = "파일 다운로드용 presigned URL 발급", description = "파일 다운로드용 presigned URL 발급입니다.")
    public ResponseEntity<ApiResponse<PresignedUrlResponseDTO>> generateDownloadPresignedUrl(@PathVariable Long imageId) {
        PresignedUrlResponseDTO response = imageService.generateDownloadPresignedUrl(imageId);
        return ResponseEntity.ok(
                ApiResponse.ok(response, "파일 다운로드용 presigned URL이 성공적으로 조회되었습니다.")
        );
    }
}