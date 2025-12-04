package com.example.muaring.domain.social.dto.post;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayPostResponseDTO {
    private Long postId;
    private Long musicId;
    private String musicName;
    private String artistName;
    private String albumImageUrl;
    private Integer likeCount;
    private Integer commentCount;
}
