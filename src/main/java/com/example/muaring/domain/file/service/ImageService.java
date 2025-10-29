package com.example.muaring.domain.file.service;

import com.example.muaring.config.S3Properties;
import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.file.entity.ImageType;
import com.example.muaring.domain.file.exception.FileErrorCode;
import com.example.muaring.domain.file.exception.FileException;
import com.example.muaring.domain.file.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final ImageRepository imageRepository;

    // ⚪ 파일 업로드용 presigned URL 생성 메서드
    public String generatePresignedUploadUrl(String fileName, ImageType imageType, Long fileSize) {
        String prefix = switch (imageType) {
            case MEMBER -> "member/";
            case GROUP -> "group/";
        };

        String uniqueFileName = UUID.randomUUID() + "_"  + fileName;
        String s3Key = prefix + uniqueFileName;

        // S3에 올릴 파일 정보 정의
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.s3().bucket())
                .key(s3Key)
                .build();

        Duration uploadDuration = Duration.ofMinutes(s3Properties.s3().presign().uploadExpMinutes());

        // 정의한 파일 정보에 서명하여 presigned URL 생성
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(uploadDuration)
                .putObjectRequest(putObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignPutObject(putObjectPresignRequest).url();

        imageRepository.save(Image.create(fileName, imageType, s3Key, fileSize));

        return presignedUrl.toString();
    }

    // ⚪ 파일 다운로드용 presigned URL 생성 메서드
    public String generatePresignedDownloadUrl(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new FileException(FileErrorCode.IMAGE_NOT_FOUND));

        // S3에서 가져올 파일 정보 정의
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.s3().bucket())
                .key(image.getS3Key())
                .build();

        Duration downloadDuration = Duration.ofMinutes(s3Properties.s3().presign().downloadExpMinutes());

        // 정의한 파일 정보에 서명하여 presigned URL 생성
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(downloadDuration)
                .getObjectRequest(getObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();
        return presignedUrl.toString();
    }
}