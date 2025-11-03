package com.example.muaring.domain.music.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class SpotifyTrackDTO {
    private String spotifyId;
    private String name;
    private String artistName;
    private String albumName;
    private String albumImgUrl;
    private Integer popularity;
}
