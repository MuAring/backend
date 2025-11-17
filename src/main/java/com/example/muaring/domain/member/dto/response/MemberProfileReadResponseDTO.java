package com.example.muaring.domain.member.dto.response;

import com.example.muaring.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberProfileReadResponseDTO(
        Long memberId,
        String nickname,
        String imageUrl,
        boolean isMe,
        boolean isPublic,
        boolean isFollowing,
        long sharedMusicCount,
        long followerCount,
        long followeeCount,
        long joinedGroupCount
) {
    public static MemberProfileReadResponseDTO from(
            Member member,
            String imageUrl,
            boolean isMe,
            boolean isPublic,
            boolean isFollowing,
            long sharedMusicCount,
            long followerCount,
            long followeeCount,
            long joinedGroupCount
            ) {
        return MemberProfileReadResponseDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .imageUrl(imageUrl)
                .isMe(isMe)
                .isPublic(isPublic)
                .isFollowing(isFollowing)
                .sharedMusicCount(sharedMusicCount)
                .followerCount(followerCount)
                .followeeCount(followeeCount)
                .joinedGroupCount(joinedGroupCount)
                .build();

    }
}