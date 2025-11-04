package com.example.muaring.domain.music.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class SpotifySearchResponse {
    private Tracks tracks;

    @Getter
    @Setter
    public static class Tracks {
        private List<Item> items;

        @Getter
        @Setter
        public static class Item {
            private String id;
            private String name;
            private Integer popularity;
            private Album album;
            private List<Artist> artists;
        }
    }

    @Getter
    @Setter
    public static class Album {
        private String name;
        private List<Image> images;
    }

    @Getter
    @Setter
    public static class Artist {
        private String name;
    }

    @Getter
    @Setter
    public static class Image {
        private String url;
    }
}
