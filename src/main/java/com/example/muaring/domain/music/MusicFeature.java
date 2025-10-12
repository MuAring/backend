package com.example.muaring.domain.music;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "music_feature")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MusicFeature {

    @Id
    @Column(name = "music_id")
    private Long musicId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "music_id")
    private Music music;

    @Column(nullable = false)
    private Double danceability;

    @Column(nullable = false)
    private Double energy;

    @Column(nullable = false)
    private Double valence;

    @Column(nullable = false)
    private Double acousticness;

    @Column(nullable = false)
    private Double instrumentalness;

    @Column(nullable = false)
    private Double speechiness;

    @Column(nullable = false)
    private Double tempo;

    @Column(nullable = false)
    private Double loudness;

    @Column(name = "music_key", nullable = false)
    private Integer musicKey;

    @Column(nullable = false)
    private Integer mode;

    @Column(name = "time_signature")
    private Integer timeSignature;

    @Column(nullable = false)
    private Double liveness;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;
}
