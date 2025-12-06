package com.example.muaring.domain.music.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicHistoryDTO {

    private Long postId;
    private Long musicId;
    private String title;
    private String artist;
    private String albumImage;
    private LocalDateTime createdAt;
}
