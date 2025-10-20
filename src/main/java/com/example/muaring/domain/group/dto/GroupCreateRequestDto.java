package com.example.muaring.domain.group.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GroupCreateRequestDto {
    private Long adminId;
    private List<Long> groupCategoryId;
    private String name;
    private String description;
    private int maxMembers;
    private Boolean opened;
}
