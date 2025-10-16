package com.example.muaring.domain.user;

import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// 사용자 음악 취향 수치화한 테이블
@Entity
@Table(name = "user_music_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserMusicProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

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

    @Column(name = "calculated_days", nullable = false)
    private Integer calculatedDays;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "avg_popularity", nullable = false)
    private Double avgPopularity;

    @Column(name = "avg_rarity")
    private Double avgRarity;

    @Column(name = "hidden_gem_ratio", nullable = false)
    private Double hiddenGemRatio;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProfileStatus status;

    // 일단 한 파일에 넣었는데 분리 필요하면 분리하기
    public enum ProfileStatus {
        PROCESSING, COMPLETED
    }
}
