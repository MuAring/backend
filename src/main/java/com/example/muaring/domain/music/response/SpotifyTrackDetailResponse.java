package com.example.muaring.domain.music.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class SpotifyTrackDetailResponse {

    private String id;
    private String name;
    private Integer popularity;

    @JsonProperty("duration_ms")
    private Integer durationMs;

    @JsonProperty("preview_url")
    private String previewUrl;

    private Album album;
    private List<Artist> artists;

    @Getter
    @NoArgsConstructor
    public static class Album {
        private String name;
        private List<Image> images;

        @JsonProperty("release_date")
        private String releaseDate;
    }

    @Getter
    @NoArgsConstructor
    public static class Artist {
        private String id;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    public static class Image {
        private String url;
        private Integer width;
        private Integer height;
    }
}

