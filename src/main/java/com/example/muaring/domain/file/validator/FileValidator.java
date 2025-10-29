package com.example.muaring.domain.file.validator;

import com.example.muaring.domain.file.dto.ImageUploadRequestDTO;
import com.example.muaring.domain.file.exception.FileErrorCode;
import com.example.muaring.domain.file.exception.FileException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FileValidator {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;  // 5MB
    private static final int MAX_IMAGE_NAME_LENGTH = 255;

    // ⚪ 이미지를 검증하기 위한 메서드
    public void validateImage(ImageUploadRequestDTO request) {
        validateImageType(request.fileType());
        validateImageSize(request.fileSize());
        validateImageName(request.fileName());
    }

    // ⚪ 파일 타입을 검증하기 위한 메서드
    private void validateImageType(String fileType) {
        if (!ALLOWED_IMAGE_TYPES.contains(fileType)) {
            throw new FileException(FileErrorCode.INVALID_FILE_TYPE);
        }
    }

    // ⚪ 파일 크기를 검증하기 위한 메서드
    private void validateImageSize(Long fileSize) {
        if (fileSize == null || fileSize > MAX_IMAGE_SIZE) {
            throw new FileException(FileErrorCode.INVALID_FILE_SIZE);
        }
    }

    // ⚪ 파일 이름을 검증하기 위한 메서드
    private void validateImageName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new FileException(FileErrorCode.INVALID_FILE_NAME_EMPTY);
        }
        if (fileName.length() > MAX_IMAGE_NAME_LENGTH) {
            throw new FileException(FileErrorCode.INVALID_FILE_NAME_TOO_LONG);
        }
    }
}