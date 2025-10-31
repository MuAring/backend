package com.example.muaring.domain.group.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GroupUpdateRequestDto {

    private String description;
    private Integer maxMembers;
    private Boolean isPublic;
    private List<String> categoryNames;
}