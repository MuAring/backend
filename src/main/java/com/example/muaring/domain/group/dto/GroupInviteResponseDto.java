package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.GroupInviteToken;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupInviteResponseDto {
    private Long inviteId;
    private String inviteUrl;
    private String inviteToken;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private String inviterName;

    public static GroupInviteResponseDto from(GroupInviteToken token, String baseUrl) {
        System.out.println("DTO 생성 - baseUrl: " + baseUrl);
        return GroupInviteResponseDto.builder()
                .inviteId(token.getId())
                .inviteUrl(baseUrl + "/" + token.getInviteToken())
                .inviteToken(token.getInviteToken())
                .createdAt(token.getCreatedAt())
                .expiresAt(token.getExpiresAt())
                .inviterName(token.getInviter().getNickname())
                .build();
    }
}