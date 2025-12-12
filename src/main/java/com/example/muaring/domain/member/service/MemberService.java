package com.example.muaring.domain.member.service;

import com.example.muaring.common.util.SecurityUtil;
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
import com.example.muaring.domain.member.dto.response.*;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.FollowRepository;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

        long sharedMusicCount = musicPostRepository.countByMemberIdAndGroupIsNullAndIsDeletedFalse(targetId);
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

    public String resolveProfileImageUrl(Member member) {
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

    public MemberSettingsReadResponse getMemberSettings(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        String imageUrl = resolveProfileImageUrl(member);
        return MemberSettingsReadResponse.of(imageUrl, member);
    }

    @Transactional(readOnly = true)
    public Page<MemberSearchItemDto> searchMembers(String name, Pageable pageable) {

        Long memberId = SecurityUtil.getMemberId();

        Page<Member> memberPage;

        if (memberId == null) {
            // 비로그인 검색 허용할 경우
            memberPage = memberRepository.findByNicknameContainingIgnoreCaseAndIsDeletedFalse(name, pageable);
        } else {
            // 로그인 상태면 나(id로 체크) 제외
            memberPage = memberRepository
                    .findByNicknameContainingIgnoreCaseAndIsDeletedFalseAndIdNot(name, memberId, pageable);
        }

        List<Member> members = memberPage.getContent();

        if (members.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, memberPage.getTotalElements());
        }

        // 내가 팔로우한 멤버 id들 한 방에 조회
        Set<Long> followedIdSet;
        if (memberId != null) {
            List<Long> followedIds = followRepository.findFolloweeIdsByFollowerId(memberId);
            followedIdSet = new HashSet<>(followedIds);
        } else {
            followedIdSet = Collections.emptySet();
        }

        // 오늘 날짜 범위 계산
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // memberIds 추출
        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .toList();

        // 오늘 MusicPost 한 번에 조회
        List<MusicPost> todayPosts = musicPostRepository.findTodayPostsByMemberIds(
                memberIds, startOfDay, endOfDay);

        // memberId -> Music 매핑
        Map<Long, MusicPost> firstPostByMember = todayPosts.stream()
                .collect(Collectors.toMap(
                        mp -> mp.getMember().getId(),
                        mp -> mp,
                        (mp1, mp2) -> {
                            // createdAt 이 더 과거인(가장 먼저 올라온) 걸 유지
                            if (mp1.getCreatedAt().isBefore(mp2.getCreatedAt())) {
                                return mp1;
                            } else {
                                return mp2;
                            }
                        }
                ));



        // DTO 리스트로 매핑
        List<MemberSearchItemDto> dtoList = members.stream()
                .map(member -> {
                    MusicPost firstPost = firstPostByMember.get(member.getId());
                    Music music = firstPost != null ? firstPost.getMusic() : null;

                    String profileImageUrl = resolveProfileImageUrl(member);

                    boolean isFollowing = memberId != null
                            && followedIdSet.contains(member.getId());

                    return MemberSearchItemDto.builder()
                            .memberId(member.getId())
                            .nickname(member.getNickname())
                            .profileImageUrl(profileImageUrl)
                            .isPublic(member.getIsPublic())
                            .isFollowing(isFollowing)
                            .todayMusicName(music != null ? music.getName() : null)
                            .todayMusicArtistName(music != null ? music.getArtistName() : null)
                            .build();
                })
                .toList();

        // Page로 감싸서 반환
        return new PageImpl<>(
                dtoList,
                pageable,
                memberPage.getTotalElements()
        );
    }

    @Transactional
    public Page<MusicHistoryDTO> getMusicHistoryByMember(Long memberId, Integer year, Integer month, Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            throw new MusicException(MusicErrorCode.MEMBER_NOT_FOUND);
        }

        Page<MusicPost> posts = musicPostRepository.findByMemberAndYearMonth(memberId, year, month, pageable);

        return posts.map(post -> MusicHistoryDTO.builder()
                .postId(post.getId())
                .musicId(post.getMusic().getId())
                .title(post.getMusic().getName())
                .artist(post.getMusic().getArtistName())
                .albumImage(post.getMusic().getAlbumImgUrl())
                .createdAt(post.getCreatedAt())
                .build());
    }
}
