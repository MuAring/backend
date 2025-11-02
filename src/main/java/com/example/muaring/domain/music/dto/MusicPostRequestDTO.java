package com.example.muaring.domain.music.dto;

import lombok.Data;

@Data
public class MusicPostRequestDTO {
    private Long memberId;
    private Long groupId;
    private Long musicId;
    private String content;
}
