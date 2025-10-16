package com.example.muaring.domain.music;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "music")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Music {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_id")
    private Long musicId;

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
