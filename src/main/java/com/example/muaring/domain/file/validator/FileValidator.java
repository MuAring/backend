package com.example.muaring.domain.file.validator;

import com.example.muaring.config.FilePolicyProperties;
import com.example.muaring.domain.file.dto.request.ImageUploadRequestDTO;
import com.example.muaring.domain.file.exception.FileErrorCode;
import com.example.muaring.domain.file.exception.FileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileValidator {

    private final FilePolicyProperties filePolicyProperties;

    // ⚪ 이미지를 검증하기 위한 메서드
    public void validateImage(ImageUploadRequestDTO request) {
        validateFileType(request.fileType());
        validateFileSize(request.fileSize());
        validateFileName(request.fileName());
    }

    // ⚪ 파일 타입을 검증하기 위한 메서드
    private void validateFileType(String fileType) {
        if (!filePolicyProperties.allowedTypes().contains(fileType)) {
            throw new FileException(FileErrorCode.INVALID_FILE_TYPE);
        }
    }

    // ⚪ 파일 크기를 검증하기 위한 메서드
    private void validateFileSize(Long fileSize) {
        if (fileSize > filePolicyProperties.maxSize()) {
            throw new FileException(FileErrorCode.INVALID_FILE_SIZE);
        }
    }

    // ⚪ 파일 이름을 검증하기 위한 메서드
    private void validateFileName(String fileName) {
        if (fileName.length() > filePolicyProperties.maxNameLength()) {
            throw new FileException(FileErrorCode.INVALID_FILE_NAME_TOO_LONG);
        }
    }
}