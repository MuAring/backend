package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.Group;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

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
                                          Map<Long, List<Long>> categoryIdsByGroup) {
        List<GroupSummaryDto> summaries = groupEntities.stream()
                .map(g -> GroupSummaryDto.of(
                        g.getId(),
                        g.getName(),
                        g.getDescription(),
                        /**
                         * 카테고리 리스트를 만듦
                         * 현재 그룹의 id에 해당하는 카테고리 리스트가 있으면 반환
                         * 없으면 빈 리스트 반환
                        */
                        categoryIdsByGroup.getOrDefault(g.getId(), List.of()),
                        g.getMemberCount(),
                        g.getMaxMembers(),
                        g.getIsPublic()
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
        private final List<Long> groupCategoryIds;
        private final int memberCount;
        private final int maxMembers;
        private final Boolean isPublic;

        public static GroupSummaryDto of(Long groupId,
                                         String name,
                                         String description,
                                         List<Long> groupCategoryIds,
                                         int memberCount,
                                         int maxMembers,
                                         Boolean isPublic) {
            return GroupSummaryDto.builder()
                    .groupId(groupId)
                    .name(name)
                    .description(description)
                    .groupCategoryIds(groupCategoryIds)
                    .memberCount(memberCount)
                    .maxMembers(maxMembers)
                    .isPublic(isPublic)
                    .build();
        }
    }
}
