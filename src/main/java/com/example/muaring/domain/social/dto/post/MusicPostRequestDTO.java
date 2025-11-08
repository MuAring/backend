package com.example.muaring.domain.social.dto.post;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicPostRequestDTO {
    private Long groupId;        // 선택적
    private String spotifyId;    // 필수
    private String content;      // 필수
}
