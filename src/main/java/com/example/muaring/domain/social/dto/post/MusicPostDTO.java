package com.example.muaring.domain.social.dto.post;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@Builder
public class MusicPostDTO {
    private Long postId;
    private Long memberId;
    private Long groupId;
    private String spotifyId;
    private String content;
}
