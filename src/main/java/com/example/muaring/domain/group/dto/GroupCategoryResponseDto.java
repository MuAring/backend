package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.GroupCategory;
import com.example.muaring.domain.group.entity.GroupCategoryType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupCategoryResponseDto {
    private final Long id;
    private final String name;
    private final String displayName;

    // seeder용 (GroupCategoryType에서 가져옴)
    public static GroupCategoryResponseDto from(GroupCategoryType type) {
        return GroupCategoryResponseDto.builder()
                .id(null)
                .name(type.getName())
                .displayName(type.getDisplayName())
                .build();
    }

    // 일반 서비스용 (GroupCategory entity에서 가져옴)
    public static GroupCategoryResponseDto from(GroupCategory entity) {
        // type 자체를 받아오기
        GroupCategoryType type = GroupCategoryType.fromName(entity.getName());
        return GroupCategoryResponseDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .displayName(type.getDisplayName())
                .build();
    }
}
