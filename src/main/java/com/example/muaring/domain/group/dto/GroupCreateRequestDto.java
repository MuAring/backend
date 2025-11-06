package com.example.muaring.domain.group.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupCreateRequestDto {
    private List<Long> groupCategoryId;
    private String name;
    private String description;
    private int maxMembers;
    private Boolean isPublic;
}
