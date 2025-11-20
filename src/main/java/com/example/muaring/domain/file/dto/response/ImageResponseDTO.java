package com.example.muaring.domain.file.dto.response;

import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.file.entity.ImageType;

public record ImageResponseDTO(
        Long id,
        String fileName,
        String fileType,
        ImageType imageType,
        String imageUrl,
        Long fileSize
) {
    public static ImageResponseDTO of(Image image, String bucketUrl) {
        if (image == null) return null;
        String url = bucketUrl + "/" + image.getS3Key();
        return new ImageResponseDTO(
                image.getId(),
                image.getFileName(),
                image.getFileType(),
                image.getImageType(),
                url,
                image.getFileSize()
        );
    }
}