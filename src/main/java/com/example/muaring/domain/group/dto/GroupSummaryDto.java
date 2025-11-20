package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.file.entity.Image;
import com.example.muaring.domain.group.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupSummaryDto {
    private Long groupId;
    private String name;
    private String description;
    private Integer memberCount;
    private Boolean isPublic;
    private String myRole;
    private LocalDateTime createdAt;
    private String imageUrl;

    public static GroupSummaryDto of(Group group, String myRole) {
        return GroupSummaryDto.builder()
                .groupId(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .memberCount(group.getMemberCount())
                .isPublic(group.getIsPublic())
                .myRole(myRole)
                .createdAt(group.getCreatedAt())
                .imageUrl(group.getImage() != null ? group.getImage().getUrl() : null)
                .build();
    }
}
