package com.example.muaring.domain.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupRecommendListResponseDto {

    private final Long memberId;   // 누구 기준 추천인지 (옵션)
    private final List<GroupRecommendItemDto> groups;

    public static GroupRecommendListResponseDto of(Long memberId,
                                                   List<GroupRecommendItemDto> groups) {
        return GroupRecommendListResponseDto.builder()
                .memberId(memberId)
                .groups(groups)
                .build();
    }
}
