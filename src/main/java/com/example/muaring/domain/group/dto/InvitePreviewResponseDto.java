package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.Group;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvitePreviewResponseDto {
    private Long groupId;
    private String groupName;
    private String groupDescription;
    private String groupImage;
    private Integer currentMembers;
    private Integer maxMembers;
    private Boolean isExpired;
    private Boolean isUsable;

    public static InvitePreviewResponseDto of(Group group, boolean isExpired, boolean isUsable) {
        return InvitePreviewResponseDto.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .groupDescription(group.getDescription())
                .groupImage(group.getGroupImage())
                .currentMembers(group.getMemberCount())
                .maxMembers(group.getMaxMembers())
                .isExpired(isExpired)
                .isUsable(isUsable)
                .build();
    }
}


