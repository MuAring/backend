package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.Group;
import lombok.Getter;

import java.util.List;

@Getter
public class GroupCreateResponseDto {
    private Long groupId;
    private Long adminId;
    private List<Long> groupCategoryId;
    private String name;
    private String description;
    private int memberCount;
    private int maxMembers;
    private Boolean opened;

    private GroupCreateResponseDto(Group entity, List<Long> categoryIds) {
        this.groupId = entity.getId();
        this.adminId = entity.getAdmin().getId();
        this.groupCategoryId = categoryIds;
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.memberCount = entity.getMemberCount();
        this.maxMembers = entity.getMaxMembers();
        this.opened = entity.getOpened();
    }

    public static GroupCreateResponseDto from(Group entity, List<Long> categoryIds) {
        return new GroupCreateResponseDto(entity, categoryIds);
    }
}
