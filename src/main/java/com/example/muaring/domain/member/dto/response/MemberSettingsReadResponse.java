package com.example.muaring.domain.member.dto.response;

import com.example.muaring.domain.member.entity.Member;

public record MemberSettingsReadResponse(
        String imageUrl,
        String nickname,
        Boolean isAccountPublic,
        Boolean isDiscoveryEnabled
) {
    public static MemberSettingsReadResponse of(String imageUrl, Member member) {
        return new MemberSettingsReadResponse(
                imageUrl,
                member.getNickname(),
                member.getIsPublic(),
                member.getDiscoveryEnabled()
        );
    }
}
