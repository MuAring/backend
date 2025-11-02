package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.Group;
import lombok.Getter;

import java.util.List;

@Getter
public class GroupCreateResponseDto {
    private final Long groupId;
    private final Long adminId;
    private final List<Long> groupCategoryId;
    private final String name;
    private final String description;
    private final int memberCount;
    private final int maxMembers;
    private final Boolean isPublic;

    private GroupCreateResponseDto(Group entity, List<Long> categoryIds) {
        this.groupId = entity.getId();
        this.adminId = entity.getAdmin().getId();
        this.groupCategoryId = categoryIds;
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.memberCount = entity.getMemberCount();
        this.maxMembers = entity.getMaxMembers();
        this.isPublic = entity.getIsPublic();
    }

    public static GroupCreateResponseDto from(Group entity, List<Long> categoryIds) {
        return new GroupCreateResponseDto(entity, categoryIds);
    }
}
