package com.example.muaring.domain.group.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_music_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMusicProfile {

    @Id
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProfileStatus status;

    @Column(name = "avg_danceability", nullable = false)
    private Double avgDanceability;

    @Column(name = "avg_energy", nullable = false)
    private Double avgEnergy;

    @Column(name = "avg_valence", nullable = false)
    private Double avgValence;

    @Column(name = "avg_acousticness", nullable = false)
    private Double avgAcousticness;

    @Column(name = "avg_instrumentalness", nullable = false)
    private Double avgInstrumentalness;

    @Column(name = "avg_speechiness", nullable = false)
    private Double avgSpeechiness;

    @Column(name = "avg_tempo", nullable = false)
    private Double avgTempo;

    @Column(name = "avg_loudness", nullable = false)
    private Double avgLoudness;

    @Column(name = "avg_popularity", nullable = false)
    private Double avgPopularity;

    @Column(name = "avg_rarity", nullable = false)
    private Double avgRarity;

    @Column(name = "hidden_gem_ratio", nullable = false)
    private Double hiddenGemRatio;

    @Column(name = "total_songs", nullable = false)
    private Integer totalSongs;

    @Column(name = "unique_tracks", nullable = false)
    private Integer uniqueTracks;

    @Column(name = "genre_diversity", nullable = false)
    private Integer genreDiversity;

    @Column(name = "active_member_ratio", nullable = false)
    private Double activeMemberRatio;

    @Column(name = "caculated_period_days", nullable = false)
    private Integer calculatedPeriodDays;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // status enum으로 관리, 필요할 시 파일 분리할 것
    public enum ProfileStatus {
        PROCESSING,
        COMPLETED
    }
}
