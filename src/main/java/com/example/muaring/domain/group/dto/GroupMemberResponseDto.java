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

    public static GroupMemberResponseDto from(GroupMember groupMember) {
        return GroupMemberResponseDto.builder()
                .memberId(groupMember.getMember().getId())
                .nickname(groupMember.getMember().getNickname())
                .profileImageUrl(groupMember.getMember().getProfileImage() != null
                        ? groupMember.getMember().getProfileImage().getUrl()
                        : null)
                .role(groupMember.getRole().name())
                .joinedAt(groupMember.getCreatedAt().toString())
                .build();
    }
}