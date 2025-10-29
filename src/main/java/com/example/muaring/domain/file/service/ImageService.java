package com.example.muaring.domain.file.service;

import com.example.muaring.config.S3Properties;
import com.example.muaring.domain.file.dto.ImageUploadRequestDTO;
import com.example.muaring.domain.file.dto.PresignedUrlResponseDTO;
import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.file.exception.FileErrorCode;
import com.example.muaring.domain.file.exception.FileException;
import com.example.muaring.domain.file.repository.ImageRepository;
import com.example.muaring.domain.file.validator.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ImageService {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;
    private final ImageRepository imageRepository;
    private final FileValidator fileValidator;

    // ⚪ 파일 업로드용 presigned URL 생성 메서드
    @Transactional
    public PresignedUrlResponseDTO generatePresignedUploadUrl(ImageUploadRequestDTO request) {
        fileValidator.validateImage(request);

        String prefix = switch (request.imageType()) {
            case MEMBER -> "member/";
            case GROUP -> "group/";
        };

        String s3Key = prefix + UUID.randomUUID();

        // S3에 올릴 파일 정보 정의
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.s3().bucket())
                .key(s3Key)
                .contentType(request.fileType())
                .contentLength(request.fileSize())
                .metadata(Map.of(
                        "original-filename", safeFileName(request.fileName()),
                        "image-type", request.fileType()
                ))
                .serverSideEncryption(ServerSideEncryption.AES256)  // S3에 저장시 데이터 자동 암호화
                .build();

        Duration uploadDuration = Duration.ofMinutes(s3Properties.s3().presign().uploadExpMinutes());

        // 정의한 파일 정보에 서명하여 presigned URL 생성
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(uploadDuration)
                .putObjectRequest(putObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignPutObject(putObjectPresignRequest).url();

//        // image DB에 메타데이터 저장은 실제 프로필 수정 등에서 구현할 예정
//        imageRepository.save(Image.create(
//                request.fileName(),
//                request.fileType(),
//                request.imageType(),
//                s3Key,
//                request.fileSize()));

        return PresignedUrlResponseDTO.of(
                presignedUrl.toString(),
                s3Key
        );
    }

    // ⚪ 파일 다운로드용 presigned URL 생성 메서드
    @Transactional
    public PresignedUrlResponseDTO generateDownloadPresignedUrl(Long imageId) {
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
        return PresignedUrlResponseDTO.of(
                presignedUrl.toString(),
                image.getS3Key()
        );
    }

    // ⚪ 파일명을 정제하는 메서드
    private String safeFileName(String fileName) {
        return fileName
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_+", "_")
                .substring(0, Math.min(fileName.length(), 100));
    }
}