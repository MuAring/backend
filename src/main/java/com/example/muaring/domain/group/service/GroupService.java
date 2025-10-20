package com.example.muaring.domain.group.service;

import com.example.muaring.domain.group.dto.GroupCreateRequestDto;
import com.example.muaring.domain.group.dto.GroupCreateResponseDto;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupCategory;
import com.example.muaring.domain.group.entity.GroupCategoryMapping;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.repository.GroupCategoryMappingRepository;
import com.example.muaring.domain.group.repository.GroupCategoryRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.group.response.GroupException;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupCategoryRepository groupCategoryRepository;
    private final GroupCategoryMappingRepository mappingRepository;
    private final MemberRepository memberRepository;

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
                    .orElseThrow(() -> new GroupException(GroupErrorCode.CATEGORY_NOT_FOUND));

            GroupCategoryMapping mapping = GroupCategoryMapping.builder()
                    .group(newGroup)
                    .groupCategory(groupCategory)
                    .build();

            mappingRepository.save(mapping);
        }

        return GroupCreateResponseDto.from(newGroup, groupCategoryIds);
    }

}
