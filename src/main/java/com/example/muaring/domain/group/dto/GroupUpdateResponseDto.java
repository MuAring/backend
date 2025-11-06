package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupCategoryMapping;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GroupUpdateResponseDto {

    private Long groupId;
    private String name;
    private String description;
    private Integer memberCount;
    private Integer maxMembers;
    private Boolean isPublic;
    private List<String> categories;

    public static GroupUpdateResponseDto from(Group group, List<GroupCategoryMapping> mappings) {
        return GroupUpdateResponseDto.builder()
                .groupId(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .memberCount(group.getMemberCount())
                .maxMembers(group.getMaxMembers())
                .isPublic(group.getIsPublic())
                .categories(mappings.stream()
                        .map(mapping -> mapping.getGroupCategory().getName())
                        .collect(Collectors.toList()))
                .build();
    }
}