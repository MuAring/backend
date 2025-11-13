package com.example.muaring.domain.file.dto.response;

public record PresignedUrlResponseDTO(
        String presignedUrl,
        String s3Key
) {
    public static PresignedUrlResponseDTO of(String presignedUrl, String s3Key) {
        return new PresignedUrlResponseDTO(presignedUrl, s3Key);
    }
}