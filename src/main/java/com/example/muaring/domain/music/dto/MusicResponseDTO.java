package com.example.muaring.domain.music.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class MusicResponseDTO {
    private Long id;
    private String spotifyId;
    private String name;
    private String artistId;
    private String artistName;
    private String albumName;
    private String albumImgUrl;
    private Integer popularity;
}
