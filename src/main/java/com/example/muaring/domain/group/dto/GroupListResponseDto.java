package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.Group;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public class GroupListResponseDto {

    private final Long totalCount;
    private final List<GroupSummaryDto> groups;

    @Builder
    public GroupListResponseDto(Long totalCount, List<GroupSummaryDto> groups) {
        this.totalCount = totalCount;
        this.groups = groups;
    }

    // dto를 생성하는 메서드
    public static GroupListResponseDto of(Long totalCount,
                                          List<Group> groupEntities,
                                          Map<Long, List<String>> categoryNamesByGroup,
                                          Set<Long> myJoinedGroupIds) {
        List<GroupSummaryDto> summaries = groupEntities.stream()
                .map(g -> GroupSummaryDto.of(
                        g.getId(),
                        g.getName(),
                        g.getDescription(),
                        // groupId에 해당하는 카테고리 이름 리스트 (없으면 빈 리스트)
                        categoryNamesByGroup.getOrDefault(g.getId(), List.of()),
                        g.getMemberCount(),
                        g.getMaxMembers(),
                        g.getIsPublic(),
                        myJoinedGroupIds != null && myJoinedGroupIds.contains(g.getId())
                ))
                .toList();

        return GroupListResponseDto.builder()
                .totalCount(totalCount)
                .groups(summaries)
                .build();
    }

    @Getter
    @Builder
    public static class GroupSummaryDto {
        private final Long groupId;
        private final String name;
        private final String description;
        private final List<String> categoryNames;  // 이름으로 변경 (displayName)
        private final int memberCount;
        private final int maxMembers;
        private final Boolean isPublic;
        private final Boolean isJoined;            // 내가 가입 중인지 여부

        public static GroupSummaryDto of(Long groupId,
                                         String name,
                                         String description,
                                         List<String> categoryNames,
                                         int memberCount,
                                         int maxMembers,
                                         Boolean isPublic,
                                         Boolean isJoined) {
            return GroupSummaryDto.builder()
                    .groupId(groupId)
                    .name(name)
                    .description(description)
                    .categoryNames(categoryNames)
                    .memberCount(memberCount)
                    .maxMembers(maxMembers)
                    .isPublic(isPublic)
                    .isJoined(isJoined)
                    .build();
        }
    }
}