package com.example.muaring.domain.music.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MusicRequestDTO {
    private String spotifyId;
    private String name;
    private String artistId;
    private String artistName;
    private String albumName;
    private String albumImgUrl;
    private Integer durationMs;
    private Integer popularity;
    private LocalDateTime releaseDate;
}

