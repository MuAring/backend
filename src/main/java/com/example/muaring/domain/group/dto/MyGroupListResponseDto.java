package com.example.muaring.domain.group.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyGroupListResponseDto {
    private Long totalCount;
    private List<GroupSummaryDto> groups;

    public static MyGroupListResponseDto of(List<GroupSummaryDto> groups) {
        return MyGroupListResponseDto.builder()
                .totalCount((long) groups.size())
                .groups(groups)
                .build();
    }
}

