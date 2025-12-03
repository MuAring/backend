package com.example.muaring.domain.file.service;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.config.S3Properties;
import com.example.muaring.domain.file.dto.request.GroupImageRequestDTO;
import com.example.muaring.domain.file.dto.request.ImageUploadRequestDTO;
import com.example.muaring.domain.file.dto.response.PresignedUrlResponseDTO;
import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.file.entity.ImageType;
import com.example.muaring.domain.file.exception.FileErrorCode;
import com.example.muaring.domain.file.exception.FileException;
import com.example.muaring.domain.file.repository.ImageRepository;
import com.example.muaring.domain.file.validator.FileValidator;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.group.response.GroupException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ImageService {

    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final ImageRepository imageRepository;
    private final FileValidator fileValidator;
    private final GroupRepository groupRepository;

    // ⚪ 파일 업로드용 presigned URL 생성 메서드
    @Transactional
    public PresignedUrlResponseDTO generatePresignedUploadUrl(ImageUploadRequestDTO request) {
        Long requesterId = SecurityUtil.getMemberId();

        // 권한 검증
        switch (request.imageType()) {
            case MEMBER -> {}  // 본인 프로필 업로드는 별도의 권한 검증 불필요
            case GROUP -> {
                Long groupId = request.targetId();
                if (groupId == null) {
                    throw new FileException(FileErrorCode.MISSING_GROUP_ID);
                }

                Group group = groupRepository.findById(groupId)
                        .orElseThrow(() -> new FileException(FileErrorCode.FORBIDDEN_GROUP_IMAGE_UPLOAD));

                if (!group.getAdmin().getId().equals(requesterId)) {
                    throw new FileException(FileErrorCode.FORBIDDEN_GROUP_IMAGE_UPLOAD);
                }
            }
        }

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
                .build();

        Duration uploadDuration = Duration.ofMinutes(s3Properties.s3().presign().uploadExpMinutes());

        // 정의한 파일 정보에 서명하여 presigned URL 생성
        PutObjectPresignRequest putObjectPresignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(uploadDuration)
                .putObjectRequest(putObjectRequest)
                .build();

        URL presignedUrl = s3Presigner.presignPutObject(putObjectPresignRequest).url();

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
        String cleaned = fileName
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_+", "_");

        return cleaned.substring(0, Math.min(cleaned.length(), 100));
    }

    // ⚪ 업로드된 s3 파일을 삭제하는 메서드
    public void deleteObject(String s3Key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.s3().bucket())
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("✅ S3 객체 삭제를 완료했습니다.");
        } catch (S3Exception e) {
            log.error("❌ S3 객체 삭제 도중 문제가 발생했습니다.: {}", s3Key, e);
            throw new RuntimeException("❌ S3 객체 삭제 도중 문제가 발생했습니다." + s3Key, e);
        }
    }


    // 그룹 프로필 이미지 업로드 메서드
    @Transactional
    public void confirmGroupImageUpload(GroupImageRequestDTO request) {
        Long requesterId = SecurityUtil.getMemberId();

        // 그룹 조회
        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        // 권한 검증 (그룹 관리자만 가능하게)
        if (!group.getAdmin().getId().equals(requesterId)) {
            throw new FileException(FileErrorCode.FORBIDDEN_GROUP_IMAGE_UPLOAD);
        }

        // S3에 파일이 실제로 존재하는지 검증
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(s3Properties.s3().bucket())
                    .key(request.getS3Key())
                    .build();

            s3Client.headObject(headObjectRequest);
        } catch (NoSuchKeyException e) {
            log.error(" S3에 파일이 존재하지 않습니다: {}", request.getS3Key());
            throw new FileException(FileErrorCode.FILE_NOT_FOUND_IN_S3);
        } catch (S3Exception e) {
            log.error(" S3 파일 확인 중 오류 발생: {}", request.getS3Key(), e);
            throw new FileException(FileErrorCode.S3_ACCESS_ERROR);
        }

        // Image 엔티티 생성 및 저장
        Image newImage = Image.create(
                request.getFileName(),
                request.getFileType(),
                ImageType.GROUP,
                request.getS3Key(),
                request.getFileSize()
        );
        imageRepository.save(newImage);

        // 기존 프로필 이미지가 있다면 S3에서 삭제
        Image oldImage = group.getImage();
        if (oldImage != null) {
            deleteObject(oldImage.getS3Key());
            imageRepository.delete(oldImage);
            log.info("기존 그룹 프로필 이미지를 삭제했습니다. S3 Key: {}", oldImage.getS3Key());
        }

        // 그룹의 프로필 이미지 업데이트
        group.updateImage(newImage);
        log.info("그룹 프로필 이미지가 설정되었습니다. Group ID: {}, Image ID: {}",
                group.getId(), newImage.getId());
    }
}