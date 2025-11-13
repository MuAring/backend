package com.example.muaring.domain.social.dto.post;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicPostResponseDTO {
    private Long postId;
    private Long memberId;
    private String memberName;
    private String content;
    private String albumImgUrl;
    private String musicName;
    private String artistName;
    private String previewUrl;
}