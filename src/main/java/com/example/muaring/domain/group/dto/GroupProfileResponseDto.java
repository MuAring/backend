package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.file.entity.Image;
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
public class GroupProfileResponseDto {

    private Long groupId;
    private String name;
    private String description;
    private List<Long> groupCategoryIds; // 그룹 카테고리 id 리스트
    private Integer totalMusicCount;     // 그룹 playlist의 음악 개수
    private Integer totalPostCount;      // 그룹 내 게시물 개수
    private Integer memberCount;
    private String imageUrl;             // 프로필 이미지 url
    private LocalDateTime createdAt;

    /**
     * Group 엔티티와 외부 계산값을 받아 Dto 생성
     */
    public static GroupProfileResponseDto of(
            Group group,
            List<Long> groupCategoryIds,
            int totalMusicCount,
            int totalPostCount
    ) {
        return GroupProfileResponseDto.builder()
                .groupId(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .groupCategoryIds(groupCategoryIds)
                .totalMusicCount(totalMusicCount)
                .totalPostCount(totalPostCount)
                .memberCount(group.getMemberCount())
                .imageUrl(group.getImage() != null ? group.getImage().getUrl() : null)
                .createdAt(group.getCreatedAt())
                .build();
    }

    /**
     * 여러 Group 엔티티를 한 번에 DTO로 변환할 때 (선택사항)
     */
    public static List<GroupProfileResponseDto> fromEntities(
            List<Group> groups,
            Map<Long, List<Long>> categoryIdsByGroup,
            Map<Long, Integer> musicCountByGroup,
            Map<Long, Integer> postCountByGroup
    ) {
        return groups.stream()
                .map(group -> GroupProfileResponseDto.of(
                        group,
                        categoryIdsByGroup.getOrDefault(group.getId(), List.of()),
                        musicCountByGroup.getOrDefault(group.getId(), 0),
                        postCountByGroup.getOrDefault(group.getId(), 0)
                ))
                .toList();
    }
}