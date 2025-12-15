package com.example.muaring.domain.group.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicArchiveDto {

    private Long musicId;
    private String title;
    private String artist;
    private String albumImage;
    private LocalDateTime firstPostedAt;  // 처음 올라온 시간

    // 필요하다면 추가 정보
    private Long postId;  // 해당 음악의 대표 포스트 ID
    private String albumName;  // 앨범명
}