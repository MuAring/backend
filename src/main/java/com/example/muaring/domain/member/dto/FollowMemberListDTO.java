package com.example.muaring.domain.member.dto;


import lombok.*;

@Getter
@Builder
public class FollowMemberListDTO {
    private Long memberId;
    private String name;
    private String profileImage;
    private Boolean isPublic;
    private String followStatus;
}
