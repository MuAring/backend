package com.example.muaring.domain.nearby.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
public class TodayNearbyMusicDTO {
    private Long memberId;
    private String profileImageUrl;
    private String musicName;
    private String artistName;
    private String albumImageUrl;
}
