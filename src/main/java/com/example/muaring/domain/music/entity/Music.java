package com.example.muaring.domain.music.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "music")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long id;

    @Column(name = "spotify_id", length = 50, nullable = false, unique = true)
    private String spotifyId;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(name = "artist_id", length = 50, nullable = false)
    private String artistId;

    @Column(name = "artist_name", length = 30, nullable = false)
    private String artistName;

    @Column(name = "album_name", length = 255, nullable = false)
    private String albumName;

    @Column(name = "album_img_url", length = 512)
    private String albumImgUrl;

    @Column(name = "duration_ms", nullable = false)
    private Integer durationMs;

    @Column(nullable = false)
    private Integer popularity;

    @Column(name = "release_date", nullable = false)
    private LocalDateTime releaseDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Music(String spotifyId, String name, String artistId, String artistName,
                 String albumName, String albumImgUrl, Integer durationMs,
                 Integer popularity, LocalDateTime releaseDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.spotifyId = spotifyId;
        this.name = name;
        this.artistId = artistId;
        this.artistName = artistName;
        this.albumName = albumName;
        this.albumImgUrl = albumImgUrl;
        this.durationMs = durationMs;
        this.popularity = popularity;
        this.releaseDate = releaseDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
