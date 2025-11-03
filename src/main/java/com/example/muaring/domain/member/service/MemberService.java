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
import com.example.muaring.domain.member.dto.response.MemberProfileResponseDTO;
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
    public MemberProfileResponseDTO registerProfile(MemberProfileCreateRequestDTO requestDTO) {
        String s3Key = requestDTO.s3Key();

        try {
            Long memberId = SecurityUtil.getMemberId();
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            if (member.getNickname() != null) {
                throw new MemberException(MemberErrorCode.ALREADY_HAS_PROFILE);
            }

            Image image = createImage(requestDTO);

            member.updateProfile(requestDTO.nickname(), image);
            return MemberProfileResponseDTO.of(member, s3Properties.s3().bucketUrl(s3Properties.region()));
        } catch (Exception e) {
            if (s3Key != null && !s3Key.isBlank()) {
                log.warn("❌ 프로필 정보 생성 도중 문제가 발생했습니다.");
                imageService.deleteObject(s3Key);
            }
            throw e;
        }
    }

    private Image createImage(MemberProfileCreateRequestDTO requestDTO) {
        // 이미지를 선택하지 않은 경우 기본 이미지 반환
        if (requestDTO.s3Key() == null || requestDTO.s3Key().isEmpty()) {
            ImageProperties.DefaultImageProperties defaultProfileImage = imageProperties.defaultProfile();
            return imageRepository.findByImageTypeAndS3Key(ImageType.MEMBER, defaultProfileImage.s3Key())
                    .orElseThrow(() -> new FileException(FileErrorCode.IMAGE_NOT_FOUND));
        }

        // 이미지를 선택한 경우 presigned 업로드 이미지 메타데이터 생성
        Image image = Image.create(
                requestDTO.fileName(),
                requestDTO.fileType(),
                ImageType.MEMBER,
                requestDTO.s3Key(),
                requestDTO.fileSize()
        );
        return imageRepository.save(image);
    }
}