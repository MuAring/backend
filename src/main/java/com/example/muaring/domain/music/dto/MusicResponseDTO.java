package com.example.muaring.domain.music.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MusicResponseDTO {
    private Long id;
    private String spotifyId;
    private String name;
    private String artistId;
    private String artistName;
    private String albumName;
    private String albumImgUrl;
    private Integer popularity;
    private Integer durationMs;
    private LocalDateTime releaseDate;
    private LocalDateTime createdAt;
}

