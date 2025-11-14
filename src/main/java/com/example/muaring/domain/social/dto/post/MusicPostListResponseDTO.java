package com.example.muaring.domain.social.dto.post;

import com.example.muaring.domain.file.entity.Image;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MusicPostListResponseDTO {
    private Long postId;
    private Long memberId;
    private String memberName;
    private Image profileImage;
    private String content;
    private String albumImgUrl;
    private String musicName;
    private String artistName;
    private String previewUrl;
    private Long likeCount;
    private Long commentCount;
}