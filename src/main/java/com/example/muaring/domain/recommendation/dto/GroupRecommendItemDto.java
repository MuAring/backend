package com.example.muaring.domain.recommendation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GroupRecommendItemDto {

    private final Long groupId;
    private final String imgUrl;
    private final String name;
    private final List<String> categoryNames;
    private final Boolean isJoined;

    public static GroupRecommendItemDto of(Long groupId,
                                           String imgUrl,
                                           String name,
                                           List<String> categoryNames,
                                           Boolean isJoined) {
        return GroupRecommendItemDto.builder()
                .groupId(groupId)
                .imgUrl(imgUrl)
                .name(name)
                .categoryNames(categoryNames)
                .isJoined(isJoined)
                .build();
    }
}
