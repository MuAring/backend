package com.example.muaring.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberSearchItemDto {

    private Long memberId;
    private String nickname;
    private String profileImageUrl;
    private Boolean isPublic;
    private Boolean isFollowing;

    // 오늘의 음악 (없으면 null)
    private String todayMusicName;
    private String todayMusicArtistName;
}
