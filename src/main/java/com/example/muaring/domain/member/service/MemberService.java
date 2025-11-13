package com.example.muaring.domain.member.service;

import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.config.ImageProperties;
import com.example.muaring.config.S3Properties;
import com.example.muaring.domain.file.entity.ImageType;
import com.example.muaring.domain.file.exception.FileErrorCode;
import com.example.muaring.domain.file.exception.FileException;
import com.example.muaring.domain.file.repository.ImageRepository;
import com.example.muaring.domain.file.service.ImageService;
import com.example.muaring.domain.member.dto.request.MemberProfileCreateRequestDTO;
import com.example.muaring.domain.member.dto.request.MemberProfileUpdateRequestDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileCreateResponseDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileUpdateResponseDTO;
import com.example.muaring.domain.member.dto.response.NicknameCheckResponseDTO;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final ImageService imageService;
    private final S3Properties s3Properties;
    private final ImageProperties imageProperties;

    public NicknameCheckResponseDTO checkNicknameDuplicated(String nickname) {
        boolean isDuplicated = memberRepository.existsByNicknameAndIsDeletedFalse(nickname);
        return NicknameCheckResponseDTO.of(nickname, isDuplicated);
    }

    @Transactional
    public MemberProfileCreateResponseDTO registerProfile(Long memberId, MemberProfileCreateRequestDTO requestDTO) {
        String s3Key = requestDTO.s3Key();

        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            if (member.getNickname() != null) {
                throw new MemberException(MemberErrorCode.ALREADY_HAS_PROFILE);
            }

            Image image = createImage(
                    requestDTO.fileName(),
                    requestDTO.fileType(),
                    requestDTO.imageType(),
                    requestDTO.s3Key(),
                    requestDTO.fileSize());

            member.createProfile(requestDTO.nickname(), image);
            return MemberProfileCreateResponseDTO.of(member, s3Properties.s3().bucketUrl(s3Properties.region()));
        } catch (Exception e) {
            if (s3Key != null && !s3Key.isBlank()) {
                log.warn("❌ 프로필 정보 생성 도중 문제가 발생했습니다.");
                imageService.deleteObject(s3Key);
            }
            throw e;
        }
    }

    private Image createImage(String fileName, String fileType, ImageType imageType, String s3Key, Long fileSize) {
        // 이미지를 선택하지 않은 경우 기본 이미지 반환
        if (s3Key == null || s3Key.isEmpty()) {
            ImageProperties.DefaultImageProperties defaultProfileImage = imageProperties.defaultProfile();
            return imageRepository.findByImageTypeAndS3Key(ImageType.MEMBER, defaultProfileImage.s3Key())
                    .orElseThrow(() -> new FileException(FileErrorCode.IMAGE_NOT_FOUND));
        }

        // 이미지를 선택한 경우 presigned 업로드 이미지 메타데이터 생성
        Image image = Image.create(
                fileName,
                fileType,
                imageType,
                s3Key,
                fileSize
        );
        return imageRepository.save(image);
    }

    @Transactional
    public MemberProfileUpdateResponseDTO updateProfile(Long memberId, MemberProfileUpdateRequestDTO requestDTO) {
        String s3Key = null;
        try {
            Member member = memberRepository.findById(memberId).
                    orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            if (requestDTO.isAllFieldNull()) {
                throw new MemberException(MemberErrorCode.EMPTY_PROFILE_UPDATE_REQUEST);
            }

            if (requestDTO.nickname() != null) {
                if (memberRepository.existsByNicknameAndIsDeletedFalse(requestDTO.nickname())) {
                    throw new MemberException(MemberErrorCode.DUPLICATE_NICKNAME);
                }
                member.updateNickname(requestDTO.nickname());
            }

            if (requestDTO.imageRequestDTO() != null) {
                s3Key = requestDTO.imageRequestDTO().s3Key();
                if (!requestDTO.isValidImageRequest()) {
                    throw new FileException(FileErrorCode.INVALID_IMAGE_REQUEST);
                }

                Image image = createImage(
                        requestDTO.imageRequestDTO().fileName(),
                        requestDTO.imageRequestDTO().fileType(),
                        requestDTO.imageRequestDTO().imageType(),
                        requestDTO.imageRequestDTO().s3Key(),
                        requestDTO.imageRequestDTO().fileSize()
                );
                member.updateProfileImage(image);
            }

            if (requestDTO.isPublic() != null) {
                member.updateIsPublic(requestDTO.isPublic());
            }

            if (requestDTO.isDiscoveryEnabled() != null) {
                member.updateDiscoveryEnabled(requestDTO.isDiscoveryEnabled());
            }

            return MemberProfileUpdateResponseDTO.from(member, s3Properties.s3().bucketUrl(s3Properties.region()));
        } catch (Exception e) {
            if (s3Key != null && !s3Key.isBlank()) {
                log.warn("❌ 프로필 정보 수정 도중 문제가 발생했습니다.");
                imageService.deleteObject(s3Key);
            }
            throw e;
        }
    }
}