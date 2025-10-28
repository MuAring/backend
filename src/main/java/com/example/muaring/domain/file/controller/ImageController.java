package com.example.muaring.domain.file.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.file.entity.ImageType;
import com.example.muaring.domain.file.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/upload-url")
    @Operation(summary = "파일 업로드용 presigned URL 발급", description = "파일 업로드용 presigned URL 발급입니다.")
    public ResponseEntity<ApiResponse<String>> getUploadUrl(
            @RequestParam String fileName,
            @RequestParam ImageType imageType,
            @RequestParam Long fileSize) {
        String presignedUploadUrl = imageService.generatePresignedUploadUrl(fileName, imageType, fileSize);
        return ResponseEntity.ok(
                ApiResponse.ok(presignedUploadUrl, "파일 업로드용 presigned URL이 성공적으로 생성되었습니다.")
        );
    }

    // 파일 다운로드용 presigned URL 발급 API
    @GetMapping("/{imageId}/download-url")
    @Operation(summary = "파일 다운로드용 presigned URL 발급", description = "파일 다운로드용 presigned URL 발급입니다.")
    public ResponseEntity<ApiResponse<String>> getDownloadUrl(@PathVariable Long imageId) {
        String presignedDownloadUrl = imageService.generatePresignedDownloadUrl(imageId);
        return ResponseEntity.ok(
                ApiResponse.ok(presignedDownloadUrl, "파일 다운로드용 presigned URL이 성공적으로 조회되었습니다.")
        );
    }
}