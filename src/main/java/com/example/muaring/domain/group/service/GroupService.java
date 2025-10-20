package com.example.muaring.domain.group.service;

import com.example.muaring.domain.group.dto.GroupCreateRequestDto;
import com.example.muaring.domain.group.dto.GroupCreateResponseDto;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupCategory;
import com.example.muaring.domain.group.entity.GroupCategoryMapping;
import com.example.muaring.domain.group.repository.GroupCategoryMappingRepository;
import com.example.muaring.domain.group.repository.GroupCategoryRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupCategoryRepository groupCategoryRepository;
    private final GroupCategoryMappingRepository mappingRepository;
    private final MemberRepository memberRepository;

    // 그룹 생성
    public GroupCreateResponseDto createGroup(GroupCreateRequestDto requestDto) {
        Member admin = memberRepository.findById(requestDto.getAdminId())
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        List<Long> groupCategoryIds = requestDto.getGroupCategoryId();
        if (groupCategoryIds == null) {
            throw new IllegalStateException("그룹 카테고리를 1개 이상 선택해주세요.");
        } else if (groupCategoryIds.size() > 3) {
            throw new IllegalStateException("그룹 카테고리는 3개 이하로 선택해야 합니다.");
        }

        Group newGroup = Group.builder()
                .admin(admin)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .maxMembers(requestDto.getMaxMembers())
                .opened(requestDto.getOpened())
                .build();

        groupRepository.save(newGroup);

        for (Long categoryId : groupCategoryIds) {
            GroupCategory groupCategory = groupCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + categoryId));

            GroupCategoryMapping mapping = GroupCategoryMapping.builder()
                    .group(newGroup)
                    .groupCategory(groupCategory)
                    .build();

            mappingRepository.save(mapping);
        }

        return GroupCreateResponseDto.from(newGroup, groupCategoryIds);
    }

}
