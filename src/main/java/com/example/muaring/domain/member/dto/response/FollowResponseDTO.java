package com.example.muaring.domain.member.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FollowResponseDTO {
    private Long followId;
    private Long followerId;
    private Long followeeId;
    private String status;
    private LocalDateTime createdAt;
}
