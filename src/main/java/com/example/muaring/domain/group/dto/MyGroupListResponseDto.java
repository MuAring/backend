package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyGroupListResponseDto {
    private Long totalCount;
    private List<MyGroupSummaryDto> groups;

    /**
     * Service 단에서 Group 리스트 + 부가 정보(Map)만 넘기면
     * 여기서 MyGroupSummaryDto 리스트로 변환해서 totalCount까지 채워주는 정적 팩토리
     */
    public static MyGroupListResponseDto of(List<Group> groupEntities,
                                            Map<Long, List<String>> categoryNamesByGroup,
                                            Map<Long, String> myRolesByGroupId) {

        List<MyGroupSummaryDto> summaries = groupEntities.stream()
                .map(g -> MyGroupSummaryDto.of(
                        g,
                        // groupId에 해당하는 카테고리 이름 리스트 (없으면 빈 리스트)
                        categoryNamesByGroup.getOrDefault(g.getId(), List.of()),
                        // 내 역할 (없으면 null 가능)
                        myRolesByGroupId != null ? myRolesByGroupId.get(g.getId()) : null
                ))
                .toList();

        return MyGroupListResponseDto.builder()
                .totalCount((long) summaries.size())
                .groups(summaries)
                .build();
    }

    @Getter
    @Builder
    public static class MyGroupSummaryDto {
        private Long groupId;
        private String name;
        private String description;
        private final List<String> categoryNames;  // 이름으로 변경 (displayName)
        private Integer memberCount;
        private Boolean isPublic;
        private String myRole;
        private LocalDateTime createdAt;
        private String imageUrl;

        public static MyGroupSummaryDto of(Group group, List<String> categoryNames, String myRole) {
            return MyGroupSummaryDto.builder()
                    .groupId(group.getId())
                    .name(group.getName())
                    .description(group.getDescription())
                    .categoryNames(categoryNames)
                    .memberCount(group.getMemberCount())
                    .isPublic(group.getIsPublic())
                    .myRole(myRole)
                    .createdAt(group.getCreatedAt())
                    .imageUrl(group.getGroupImage())
                    .build();
        }
    }
}

