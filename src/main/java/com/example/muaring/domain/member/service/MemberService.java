package com.example.muaring.domain.member.service;

import com.example.muaring.config.ImageProperties;
import com.example.muaring.config.S3Properties;
import com.example.muaring.domain.file.dto.request.ImageCreateRequestDTO;
import com.example.muaring.domain.file.entity.ImageType;
import com.example.muaring.domain.file.exception.FileErrorCode;
import com.example.muaring.domain.file.exception.FileException;
import com.example.muaring.domain.file.repository.ImageRepository;
import com.example.muaring.domain.file.service.ImageService;
import com.example.muaring.domain.group.repository.GroupMemberRepository;
import com.example.muaring.domain.member.dto.request.MemberProfileCreateRequestDTO;
import com.example.muaring.domain.member.dto.request.MemberProfileUpdateRequestDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileCreateResponseDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileReadResponseDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileUpdateResponseDTO;
import com.example.muaring.domain.member.dto.response.NicknameCheckResponseDTO;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.FollowRepository;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberService {

    private final ImageService imageService;
    private final S3Properties s3Properties;
    private final ImageProperties imageProperties;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final MusicPostRepository musicPostRepository;
    private final FollowRepository followRepository;
    private final GroupMemberRepository groupMemberRepository;

    public NicknameCheckResponseDTO checkNicknameDuplicated(String nickname) {
        boolean isDuplicated = memberRepository.existsByNicknameAndIsDeletedFalse(nickname);
        return NicknameCheckResponseDTO.of(nickname, isDuplicated);
    }

    @Transactional
    public MemberProfileCreateResponseDTO registerProfile(Long memberId, MemberProfileCreateRequestDTO requestDTO) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            if (member.getNickname() != null) {
                throw new MemberException(MemberErrorCode.ALREADY_HAS_PROFILE);
            }

            Image image = createImageIfExists(requestDTO.imageRequest());

            member.createProfile(requestDTO.nickname(), image);
            return MemberProfileCreateResponseDTO.of(member, s3Properties.s3().bucketUrl(s3Properties.region()));
        } catch (Exception e) {
            if (requestDTO.imageRequest() != null
                    && requestDTO.imageRequest().isCompleteImage()) {
                log.warn("❌ 프로필 정보 생성 도중 문제가 발생했습니다.");
                imageService.deleteObject(requestDTO.imageRequest().s3Key());
            }
            throw e;
        }
    }

    private Image createImageIfExists(ImageCreateRequestDTO requestDTO) {
        // 이미지를 선택하지 않은 경우 기본 이미지 반환
        if (requestDTO == null || !requestDTO.isCompleteImage()) {
            return null;
        }

        // 이미지를 선택한 경우 presigned 업로드 이미지 메타데이터 생성
        Image image = Image.create(
                requestDTO.fileName(),
                requestDTO.fileType(),
                requestDTO.imageType(),
                requestDTO.s3Key(),
                requestDTO.fileSize()
        );
        return imageRepository.save(image);
    }

    @Transactional
    public MemberProfileUpdateResponseDTO updateProfile(Long memberId, MemberProfileUpdateRequestDTO requestDTO) {
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

            if (requestDTO.imageRequestDTO() != null && requestDTO.imageRequestDTO().isCompleteImage()) {
                if (!requestDTO.isValidImageRequest()) {
                    throw new FileException(FileErrorCode.INVALID_IMAGE_REQUEST);
                }

                Image image = createImageIfExists(requestDTO.imageRequestDTO());
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
            if (requestDTO.imageRequestDTO() != null
                    && requestDTO.imageRequestDTO().isCompleteImage()) {
                log.warn("❌ 프로필 정보 수정 도중 문제가 발생했습니다.");
                imageService.deleteObject(requestDTO.imageRequestDTO().s3Key());
            }
            throw e;
        }
    }

    public MemberProfileReadResponseDTO getProfile(Long targetId, Long loginMemberId) {
        Member member = memberRepository.findById(targetId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        String imageUrl = resolveProfileImageUrl(member);

        boolean isMe = loginMemberId.equals(member.getId());  // 내 프로필 여부
        boolean isPublic = member.getIsPublic();  // 비공개 계정 여부
        boolean isFollowing = followRepository.existsByFollowerIdAndFolloweeId(loginMemberId, targetId);  // 팔로우 여부

        long sharedMusicCount = musicPostRepository.countByMemberIdAndIsDeletedIsFalse(targetId);
        long followerCount = followRepository.countByFolloweeId(targetId); // targetId 회원을 팔로우하는 사람 수
        long followeeCount = followRepository.countByFollowerId(targetId);  // targetId 회원이 팔로우하는 사람 수
        long joinedGroupCount = groupMemberRepository.countByMemberId(targetId);

        return MemberProfileReadResponseDTO.from(
                member,
                imageUrl,
                isMe,
                isPublic,
                isFollowing,
                sharedMusicCount,
                followerCount,
                followeeCount,
                joinedGroupCount
        );
    }

    private String resolveProfileImageUrl(Member member) {
        Image image = member.getProfileImage();

        if (image != null) {
            return imageService
                    .generateDownloadPresignedUrl(image.getId())
                    .presignedUrl();
        }

        Image defaultImage = imageRepository.findByImageTypeAndS3Key(
                ImageType.MEMBER,
                imageProperties.defaultProfile().s3Key()
        ).orElseThrow(() -> new FileException(FileErrorCode.IMAGE_NOT_FOUND));

        return imageService
                .generateDownloadPresignedUrl(defaultImage.getId())
                .presignedUrl();
    }
}
