package com.example.muaring.domain.group.service;

import com.example.muaring.domain.group.dto.*;
import com.example.muaring.domain.group.entity.*;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.repository.*;
import com.example.muaring.domain.group.response.GroupException;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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

    // 그룹 생성
    @Transactional
    public GroupCreateResponseDto createGroup(GroupCreateRequestDto requestDto) {
        Member admin = memberRepository.findById(requestDto.getAdminId())
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

    // 각 조건에 따라 그룹을 조회할 수 있는 메서드
    @Transactional
    public GroupListResponseDto getGroups(
            String name,            // 검색어
            Boolean isPublic,       // 공개 여부
            List<Long> categoryIds, // 카테고리 필터
            int page,               // 페이지 번호
            int size,               // 페이지 크기
            Sort sort               // 정렬 조건
    ) {
        Pageable pageable = PageRequest.of(page, size, sort);   // 0-based

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


        // 매핑 테이블 한 번에 조회 → Map<groupId, List<categoryId>> 형태로 변환
        Map<Long, List<Long>> categoryIdsByGroup = mappingRepository.findPairsByGroupIds(groupIds)
                .stream()
                .collect(Collectors.groupingBy(
                        GroupIdCategoryIdProjection::getGroupId,
                        Collectors.mapping(GroupIdCategoryIdProjection::getGroupCategoryId, Collectors.toList())
                ));

        // DTO 조립
        return GroupListResponseDto.of(
                groupPage.getTotalElements(),   // 전체 개수
                groups,                         // 현재 페이지 그룹 리스트
                categoryIdsByGroup              // 그룹별 카테고리 매핑
        );
    }

    // 내 소속 그룹 조회 메서드
    @Transactional
    public MyGroupListResponseDto getMyGroups(Long memberId) {
        List<MyGroupSummaryDto> groups = groupMemberRepository.findByMember_IdOrderByGroup_NameAsc(memberId)
                .stream()
                .map(tuple -> MyGroupSummaryDto.of(
                        tuple.getGroup(),
                        tuple.getRole().name()
                ))
                .toList();

        return MyGroupListResponseDto.of(groups);
    }

    // 그룹 상세 조회 메서드

}
