package com.example.muaring.domain.group.service;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.group.dto.*;
import com.example.muaring.domain.group.entity.*;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.level.GroupActivityType;
import com.example.muaring.domain.group.level.GroupLevelService;
import com.example.muaring.domain.group.repository.*;
import com.example.muaring.domain.group.repository.projection.GroupIdCategoryNameProjection;
import com.example.muaring.domain.group.response.GroupException;
import com.example.muaring.domain.library.repository.LibraryRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import com.example.muaring.domain.member.service.MemberService;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.social.dto.post.MusicPostFeedResponseDto;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.exception.post.PostErrorCode;
import com.example.muaring.domain.social.exception.post.PostException;
import com.example.muaring.domain.social.repository.LikeRepository;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.muaring.domain.group.entity.GroupMember.GroupRole.ADMIN;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupCategoryRepository groupCategoryRepository;
    private final GroupCategoryMappingRepository mappingRepository;
    private final MemberRepository memberRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMusicProfileRepository groupMusicProfileRepository;
    private final MusicPostRepository musicPostRepository;
    private final LikeRepository likeRepository;
    private final GroupPlaylistRepository groupPlaylistRepository;
    private final GroupLevelService groupLevelService;
    private final LibraryRepository libraryRepository;
    private final MemberService memberService;

    // 🍀 그룹 생성
    @Transactional
    public GroupCreateResponseDto createGroup(GroupCreateRequestDto requestDto, Long adminId) {
        Member admin = memberRepository.findById(adminId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Long> groupCategoryIds = requestDto.getGroupCategoryId();
        if (groupCategoryIds == null) {
            throw new GroupException(GroupErrorCode.CATEGORY_SELECTION_REQUIRED);
        } else if (groupCategoryIds.size() > 3) {
            throw new GroupException(GroupErrorCode.CATEGORY_SELECTION_EXCEEDED);
        }

        // 그룹 생성
        Group newGroup = Group.builder()
                .admin(admin)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .maxMembers(requestDto.getMaxMembers())
                .isPublic(requestDto.getIsPublic())
                .build();

        newGroup = groupRepository.saveAndFlush(newGroup);

        // 그룹-카테고리 관계 생성
        for (Long categoryId : groupCategoryIds) {
            GroupCategory groupCategory = groupCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new GroupException(GroupErrorCode.CATEGORY_NOT_FOUND));

            GroupCategoryMapping mapping = GroupCategoryMapping.builder()
                    .group(newGroup)
                    .groupCategory(groupCategory)
                    .build();

            mappingRepository.save(mapping);
        }

        // 그룹-멤버 관계 생성
        GroupMember groupMember = GroupMember.builder()
                .group(newGroup)
                .member(admin)
                .role(ADMIN)
                .build();

        groupMemberRepository.save(groupMember);

        // 그룹-음악 성향 생성
        GroupMusicProfile profile = GroupMusicProfile.createEmpty(newGroup);
        groupMusicProfileRepository.save(profile);

        return GroupCreateResponseDto.from(newGroup, groupCategoryIds);
    }


    // 🍀 각 조건에 따라 그룹을 조회
    @Transactional(readOnly = true)
    public GroupListResponseDto getGroups(
            String name,            // 검색어
            Boolean isPublic,       // 공개 여부
            List<Long> categoryIds, // 카테고리 필터
            int page,               // 페이지 번호
            int size,               // 페이지 크기
            Sort sort               // 정렬 조건
    ) {
        Pageable pageable = PageRequest.of(page, size, sort);   // 0-based
        Long memberId = SecurityUtil.getMemberId();

        // 빈 리스트일 경우, categoryIds를 null로 변경 (IN () 방지)
        List<Long> normalizedCategoryIds =
                (categoryIds == null || categoryIds.isEmpty()) ? null : categoryIds;

        // Repository 단 페이지네이션 조회
        Page<Group> groupPage = groupRepository.search(name, isPublic, normalizedCategoryIds, pageable);
        List<Group> groups = groupPage.getContent();    // 컨텐츠 받아오기

        // 결과가 비어있으면 빈 DTO 반환
        if (groups.isEmpty()) {
            return GroupListResponseDto.builder()
                    .totalCount(0L)
                    .groups(List.of())
                    .build();
        }

        // 그룹 id 리스트
        List<Long> groupIds = groups.stream()
                .map(Group::getId)
                .toList();

        // 1) groupId -> [카테고리 displayName] 매핑
        Map<Long, List<String>> categoryNamesByGroup =
                mappingRepository.findPairsWithNamesByGroupIds(groupIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                GroupIdCategoryNameProjection::getGroupId,
                                Collectors.mapping(
                                        projection -> {
                                            String code = projection.getCategoryCode(); // "k_pop", "indie", ...
                                            return GroupCategoryType.fromName(code)
                                                    .getDisplayName();                 // "케이팝", "인디 / 어쿠스틱", ...
                                        },
                                        Collectors.toList()
                                )
                        ));

        // 2) 내가 가입한 그룹 id들
        Set<Long> myJoinedGroupIds = Collections.emptySet();
        if (memberId != null) {
            myJoinedGroupIds = groupMemberRepository.findGroupIdsByMemberId(memberId);
        }

        // 3) DTO 조립
        return GroupListResponseDto.of(
                groupPage.getTotalElements(),
                groups,
                categoryNamesByGroup,
                myJoinedGroupIds
        );
    }


    // 🍀 내 소속 그룹 조회 메서드 (검색도 추가)
    @Transactional(readOnly = true)
    public MyGroupListResponseDto getMyGroups(String name) {

        Long memberId = SecurityUtil.getMemberId();
        if (memberId == null) {
            throw new GroupException(GroupErrorCode.UNAUTHORIZED_MEMBER);
        }

        // 1) 내가 속한 그룹 + role 조회 (GroupMember 기준)
        List<GroupMember> memberships = (name == null || name.isBlank())
                ? groupMemberRepository.findByMember_IdOrderByGroup_NameAsc(memberId)
                : groupMemberRepository.findByMember_IdAndGroup_NameContainingIgnoreCaseOrderByGroup_NameAsc(memberId, name);

        // Group 리스트로 변환
        List<Group> groups = memberships.stream()
                .map(GroupMember::getGroup)
                .toList();

        List<Long> groupIds = groups.stream()
                .map(Group::getId)
                .toList();

        // 2) groupId -> 카테고리 displayName 리스트 매핑
        Map<Long, List<String>> categoryNamesByGroup = groupIds.isEmpty()
                ? Map.of()
                : mappingRepository.findPairsWithNamesByGroupIds(groupIds)
                .stream()
                .collect(Collectors.groupingBy(
                        GroupIdCategoryNameProjection::getGroupId,
                        Collectors.mapping(
                                projection -> {
                                    String code = projection.getCategoryCode(); // "k_pop", "indie", ...
                                    return GroupCategoryType.fromName(code)
                                            .getDisplayName();                 // "케이팝", "인디 / 어쿠스틱", ...
                                },
                                Collectors.toList()
                        )
                ));

        // 3) groupId -> 내 role 매핑 (projection 안 쓰고 바로 GroupMember에서 꺼냄)
        Map<Long, String> myRolesByGroupId = memberships.stream()
                .collect(Collectors.toMap(
                        gm -> gm.getGroup().getId(),
                        gm -> gm.getRole().name()   // "ADMIN" / "MEMBER"
                ));

        // 4) DTO 변환
        return MyGroupListResponseDto.of(
                groups,               // List<Group>
                categoryNamesByGroup, // Map<Long, List<String>>
                myRolesByGroupId      // Map<Long, String>
        );
    }


    // 🍀 그룹 상세 조회 메서드
    @Transactional(readOnly = true)
    public GroupProfileResponseDto getGroupProfile(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        int totalPostCount = musicPostRepository.countActiveByGroupId(groupId);
        int totalMusicCount = groupPlaylistRepository.countByGroupId(groupId);

        // 1) groupId 하나를 리스트로 감싸서 조회
        List<GroupIdCategoryNameProjection> mappings =
                mappingRepository.findPairsWithNamesByGroupIds(List.of(groupId));

        // 2) projection → category displayName 리스트로 변환
        List<String> categoryNames = mappings.stream()
                .map(projection -> {
                    String code = projection.getCategoryCode(); // "k_pop", "indie" ...
                    return GroupCategoryType.fromName(code)     // enum 매핑
                            .getDisplayName();                  // "케이팝", "인디" ...
                })
                .distinct() // 같은 카테고리 중복 방지
                .toList();

        return GroupProfileResponseDto.of(
                group,
                categoryNames,      // 이름 리스트로 전달
                totalMusicCount,
                totalPostCount
        );
    }


    // 🍀 그룹 내 피드 조회 메서드
    @Transactional(readOnly = true)
    public Page<MusicPostFeedResponseDto> getGroupFeed(
            Long groupId,
            Integer year,
            Integer month,
            Pageable pageable
    ) {

        Long viewerId = SecurityUtil.getMemberId();

        // 그룹 멤버 권한 확인
        if (viewerId == null || !groupMemberRepository.existsByGroup_IdAndMember_Id(groupId, viewerId)) {
            throw new GroupException(GroupErrorCode.NOT_GROUP_MEMBER);
        }

        Page<MusicPost> posts;

        // year & month 있으면 → 특정 월 조회
        if (year != null && month != null) {
            YearMonth yearMonth = YearMonth.of(year, month);

            LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
            LocalDateTime end = yearMonth.plusMonths(1).atDay(1).atStartOfDay();

            posts = musicPostRepository.findByGroup_IdAndCreatedAtBetween(
                    groupId,
                    start,
                    end,
                    pageable
            );
        }
        // 둘 다 없으면 전체 조회
        else {
            posts = musicPostRepository.findByGroup_Id(groupId, pageable);
        }

        return mapToFeedDtosWithLibraryFlag(posts, viewerId);
    }


    // 🍀 그룹 내 오늘의 피드 조회
    @Transactional(readOnly = true)
    public Page<MusicPostFeedResponseDto> getTodayGroupFeed(
            Long groupId,
            Pageable pageable
    ) {
        Long viewerId = SecurityUtil.getMemberId();

        // 그룹 멤버 권한 확인
        if (viewerId == null || !groupMemberRepository.existsByGroup_IdAndMember_Id(groupId, viewerId)) {
            throw new GroupException(GroupErrorCode.NOT_GROUP_MEMBER);
        }

        LocalDate today = LocalDate.now();

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        Page<MusicPost> posts = musicPostRepository
                .findByGroup_IdAndCreatedAtBetween(groupId, start, end, pageable);

        return mapToFeedDtosWithLibraryFlag(posts, viewerId);
    }


    // 피드에 보관함 여부 + 좋아요 눌렀는지 여부 추가
    private Page<MusicPostFeedResponseDto> mapToFeedDtosWithLibraryFlag(
            Page<MusicPost> posts,
            Long viewerId
    ) {

        List<MusicPost> content = posts.getContent();
        if (content.isEmpty()) {
            return posts.map(post -> MusicPostFeedResponseDto.from(post, false, false));
        }

        // postIds, musicIds 추출
        List<Long> postIds = content.stream()
                .map(MusicPost::getId)
                .toList();

        List<Long> musicIds = content.stream()
                .map(p -> p.getMusic().getId())
                .toList();

        // 내 보관함에 있는 musicId 목록 조회
        List<Long> inLibraryIds = libraryRepository.findMusicIdsInLibrary(viewerId, musicIds);
        Set<Long> inLibrarySet = new HashSet<>(inLibraryIds);

        // 내가 좋아요 누른 postId 목록 조회
        List<Long> likedPostIds = likeRepository.findLikedPostIds(viewerId, postIds);
        Set<Long> likedSet = new HashSet<>(likedPostIds);

        // 같은 멤버 여러 번 나올 수 있으니까 캐시
        Map<Long, String> profileUrlCache = new HashMap<>();

        return posts.map(post -> {
            boolean inLibrary = inLibrarySet.contains(post.getMusic().getId());
            boolean liked = likedSet.contains(post.getId());

            Member member = post.getMember();
            Long memberId = member.getId();

            // presigned URL 캐싱해서 member당 한 번만 생성
            String profileImageUrl = profileUrlCache.computeIfAbsent(
                    memberId,
                    id -> memberService.resolveProfileImageUrl(member)
            );

            return MusicPostFeedResponseDto.from(post, inLibrary, liked, profileImageUrl);
        });
    }


    // 🍀 그룹 히스토리 조회 메서드
    @Transactional(readOnly = true)
    public Page<MusicHistoryDTO> getMusicHistoryByGroup(
            Long groupId,
            Integer year,
            Integer month,
            Pageable pageable
    ) {
        // 1. 그룹 존재 여부 체크 (선택사항이지만 있으면 좋음)
        if (!groupRepository.existsById(groupId)) {
            throw new GroupException(GroupErrorCode.GROUP_NOT_FOUND);
        }

        // 2. year, month 없으면 -> 현재 달 기준
        YearMonth targetMonth;
        if (year != null && month != null) {
            targetMonth = YearMonth.of(year, month);
        } else {
            targetMonth = YearMonth.now();
        }

        LocalDateTime start = targetMonth.atDay(1).atStartOfDay();
        LocalDateTime end = targetMonth.plusMonths(1).atDay(1).atStartOfDay();

        // 3. 해당 월의 전체 포스트 가져오기 (삭제되지 않은 것만, 시간 오름차순)
        List<MusicPost> posts = musicPostRepository
                .findByGroup_IdAndIsDeletedFalseAndCreatedAtBetweenOrderByCreatedAtAsc(
                        groupId,
                        start,
                        end
                );

        // 4. 하루당 "가장 빠른" 포스트만 선택
        //    createdAt ASC로 정렬되어 있으니, 날짜별로 첫 번째 것만 선택하면 됨
        Map<LocalDate, MusicPost> earliestPostPerDay = new LinkedHashMap<>();

        for (MusicPost post : posts) {
            LocalDate day = post.getCreatedAt().toLocalDate();
            // 처음 본 날짜일 때만 put (이미 있으면 더 늦게 올라온 거라 무시)
            earliestPostPerDay.putIfAbsent(day, post);
        }

        // 5. MusicHistoryDTO 리스트로 변환 (날짜 순 유지)
        List<MusicHistoryDTO> dailyHistories = earliestPostPerDay.values().stream()
                .map(post -> MusicHistoryDTO.builder()
                        .postId(post.getId())
                        .musicId(post.getMusic().getId())
                        .title(post.getMusic().getName())
                        .artist(post.getMusic().getArtistName())
                        .albumImage(post.getMusic().getAlbumImgUrl())
                        .createdAt(post.getCreatedAt())
                        .build()
                )
                .toList();

        // 6. Page 처리 (in-memory pagination)
        int total = dailyHistories.size();
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<MusicHistoryDTO> pageContent;
        if (fromIndex >= total) {
            pageContent = List.of();
        } else {
            pageContent = dailyHistories.subList(fromIndex, toIndex);
        }

        return new PageImpl<>(pageContent, pageable, total);
    }


    // 🍀 공개 그룹 가입 (별도 요청 X)
    @Transactional
    public void joinPublicGroup(Long groupId) {

        Long memberId = SecurityUtil.getMemberId();

        // 존재 여부 체크
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        // 공개 여부 체크 (공개 그룹일 경우만 이 메서드로 가입 가능)
        if (!group.isPublic()) {
            throw new GroupException(GroupErrorCode.NOT_PUBLIC_GROUP);
        }

        // 이미 그룹 멤버인지 확인
        if (groupMemberRepository.existsByGroupIdAndMemberId(group.getId(), memberId)) {
            throw new GroupException(GroupErrorCode.ALREADY_GROUP_MEMBER);
        }

        // 그룹이 꽉 찼는지 확인
        if (group.isFull()) {
            throw new GroupException(GroupErrorCode.GROUP_FULL);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.MEMBER_NOT_FOUND));

        // 그룹 멤버 추가
        GroupMember groupMember = GroupMember.builder()
                .group(group)
                .member(member)
                .role(GroupMember.GroupRole.MEMBER)
                .build();

        groupMemberRepository.save(groupMember);

        // 새로 가입하면 경험치 쌓임
        groupLevelService.addActivity(
                group.getId(),
                memberId,
                GroupActivityType.MEMBER_JOINED
        );

        group.incrementMemberCount();
    }


    // 그룹 멤버 조회 메서드
    @Transactional
    public List<GroupMemberResponseDto> getGroupMembers(Long groupId, Long memberId, String search) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        // 비공개 그룹일 경우에는 요청자가 그룹 멤버인지 확인
        if (!group.getIsPublic()) {
            boolean isMember = groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId);
            if (!isMember) {
                throw new GroupException(GroupErrorCode.NOT_GROUP_MEMBER);
            }
        }

        List<GroupMember> groupMembers;

        // 검색어가 있으면 필터링
        if (search != null && !search.trim().isEmpty()) {
            groupMembers = groupMemberRepository.findByGroupIdAndMemberNicknameContaining(groupId, search.trim());
        } else {
            groupMembers = groupMemberRepository.findByGroupId(groupId);
        }

        // 모든 멤버 ID 수집
        List<Long> memberIds = groupMembers.stream()
                .map(gm -> gm.getMember().getId())
                .toList();

        // 한 번에 최신 게시물 조회
        List<MusicPost> latestPosts = musicPostRepository.findLatestPostsByMemberIds(memberIds);

        // memberId를 키로 하는 Map 생성
        Map<Long, MusicPost> latestPostMap = latestPosts.stream()
                .collect(Collectors.toMap(
                        post -> post.getMember().getId(),
                        post -> post
                ));

        return groupMembers.stream()
                .map(groupMember -> {
                    GroupMemberResponseDto.GroupMemberResponseDtoBuilder builder =
                            GroupMemberResponseDto.builder()
                                    .memberId(groupMember.getMember().getId())
                                    .nickname(groupMember.getMember().getNickname())
                                    .profileImageUrl(groupMember.getMember().getProfileImage() != null
                                            ? groupMember.getMember().getProfileImage().getUrl()
                                            : null)
                                    .role(groupMember.getRole().name())
                                    .joinedAt(groupMember.getCreatedAt().toString());

                    // Map에서 해당 멤버의 최신 게시물 찾기
                    MusicPost latestPost = latestPostMap.get(groupMember.getMember().getId());
                    if (latestPost != null) {
                        GroupMemberResponseDto.RecentMusicDto recentMusic =
                                GroupMemberResponseDto.RecentMusicDto.builder()
                                        .musicId(latestPost.getMusic().getId())
                                        .postId(latestPost.getId())
                                        .musicName(latestPost.getMusic().getName())
                                        .artistName(latestPost.getMusic().getArtistName())
                                        .albumImgUrl(latestPost.getMusic().getAlbumImgUrl())
                                        .build();
                        builder.recentMusic(recentMusic);
                    }

                    return builder.build();
                })
                .toList();
    }


    // 그룹 정보 수정 메서드
    @Transactional
    public GroupUpdateResponseDto updateGroup(Long groupId, Long memberId, GroupUpdateRequestDto request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        if (!group.getAdmin().getId().equals(memberId)) {
            throw new GroupException(GroupErrorCode.NOT_GROUP_ADMIN);
        }

        // 설명 수정
        if (request.getDescription() != null) {
            group.updateDescription(request.getDescription());
        }

        // 최대 인원 수정
        if (request.getMaxMembers() != null) {
            if (request.getMaxMembers() < group.getMemberCount()) {
                throw new GroupException(GroupErrorCode.MAX_MEMBERS_TOO_SMALL);
            }
            group.updateMaxMembers(request.getMaxMembers());
        }

        // 공개 여부 수정
        if (request.getIsPublic() != null) {
            group.updateIsPublic(request.getIsPublic());
        }

        // 그룹 카테고리 수정
        if (request.getCategoryNames() != null) {
            if (request.getCategoryNames().size() != 3) {
                throw new GroupException(GroupErrorCode.CATEGORY_MUST_BE_THREE);
            }

            // 기존 매핑 삭제
            mappingRepository.deleteByGroup(group);

            List<GroupCategory> categories = request.getCategoryNames().stream()
                    .map(name -> groupCategoryRepository.findByName(name)
                            .orElseThrow(() -> new GroupException(GroupErrorCode.CATEGORY_NOT_FOUND)))
                    .collect(Collectors.toList());

            List<GroupCategoryMapping> newMappings = categories.stream()
                    .map(category -> GroupCategoryMapping.builder()
                            .group(group)
                            .groupCategory(category)
                            .build())
                    .collect(Collectors.toList());

            mappingRepository.saveAll(newMappings);
        }

        List<GroupCategoryMapping> mappings = mappingRepository.findByGroup(group);
        return GroupUpdateResponseDto.from(group, mappings);
    }


    // 그룹 삭제 메서드
    @Transactional
    public void deleteGroup(Long groupId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        // 관리자 권한 확인
        if (!group.getAdmin().getId().equals(memberId)) {
            throw new GroupException(GroupErrorCode.NOT_GROUP_ADMIN);
        }

        group.softDelete();
    }


    // 그룹 탈퇴 메서드
    @Transactional
    public void leaveGroup(Long groupId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        // 그룹 멤버 확인
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.NOT_GROUP_MEMBER));

        groupMemberRepository.delete(groupMember);

        // 그룹 멤버 수 감소
        group.decrementMemberCount();
    }


    // 관리자 탈퇴 시 관리자 지정 메서드
    public void adminLeaveGroup(Long groupId, Long memberId, AdminLeaveRequestDto request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        GroupMember currentMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.NOT_GROUP_MEMBER));

        // 현재 사용자가 관리자인지 확인
        if (currentMember.getRole() != ADMIN) {
            throw new GroupException(GroupErrorCode.NOT_GROUP_ADMIN);
        }

        // 자기 자신을 새 관리자로 지정하는지 확인
        if (memberId.equals(request.getNewAdminId())) {
            throw new GroupException(GroupErrorCode.CANNOT_TRANSFER_TO_SELF);
        }

        GroupMember newAdmin = groupMemberRepository.findByGroupIdAndMemberId(groupId, request.getNewAdminId())
                .orElseThrow(() -> new GroupException(GroupErrorCode.NOT_GROUP_MEMBER));

        Member newAdminMember = memberRepository.findById(request.getNewAdminId())
                .orElseThrow(() -> new GroupException(GroupErrorCode.MEMBER_NOT_FOUND));

        newAdmin.updateRole(ADMIN);

        group.updateAdmin(newAdminMember);

        groupMemberRepository.delete(currentMember);

        // 그룹 멤버 수 감소
        group.decrementMemberCount();
    }


    // 그룹 멤버 추방 메서드
    @Transactional
    public void expelMember(Long groupId, Long adminId, Long expellerId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        GroupMember adminMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, adminId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.NOT_GROUP_MEMBER));

        if (adminMember.getRole() != ADMIN) {
            throw new GroupException(GroupErrorCode.NOT_GROUP_ADMIN);
        }

        GroupMember expelMember = groupMemberRepository.findByGroupIdAndMemberId(groupId, expellerId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.NOT_GROUP_MEMBER));

        // 자기 자신 추방 방지
        if (adminId.equals(expellerId)) {
            throw new GroupException(GroupErrorCode.CANNOT_EXPEL_SELF);
        }

        // 이미 삭제된 멤버인지 확인
        if (expelMember.getIsDeleted()) {
            throw new GroupException(GroupErrorCode.ALREADY_EXPELLED_MEMBER);
        }

        expelMember.softDelete();

        // 그룹 멤버 수 감소
        group.decrementMemberCount();
    }
}