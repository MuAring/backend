package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.GroupMember;
import com.example.muaring.domain.group.entity.GroupMember.GroupRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupMemberResponseDto {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
        private String role;
        private String joinedAt;


    // 최신 음악 정보 추가
    private RecentMusicDto recentMusic;

    @Getter
    @Builder
    public static class RecentMusicDto {
        private Long musicId;
        private String musicName;
        private String artistName;
        private String albumImgUrl;
        private Long postId;
    }

    public static GroupMemberResponseDto from(GroupMember groupMember) {
        return GroupMemberResponseDto.builder()
                .memberId(groupMember.getMember().getId())
                .nickname(groupMember.getMember().getNickname())
                .profileImageUrl(groupMember.getMember().getProfileImage() != null
                        ? groupMember.getMember().getProfileImage().getUrl()
                        : null)
                .role(groupMember.getRole().name())
                .joinedAt(groupMember.getCreatedAt().toString())
                .recentMusic(null) // 서비스에서 설정할 예정
                .build();
    }
}