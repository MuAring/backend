package com.example.muaring.domain.stats;

import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "monthly_user_stats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "year_month"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MonthlyUserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long statId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 연월 (ex: 2025-09)
    @Column(name = "year_month", length = 10, nullable = false)
    private String yearMonth;

    // 한 달간 들은 곡 총 수
    @Column(name = "total_songs", nullable = false)
    private Integer totalSongs;

    // 연속 감상 일수
    @Column(name = "consecutive_days", nullable = false)
    private Integer consecutiveDays;

    // 다양한 장르 수
    @Column(name = "genre_diversity", nullable = false)
    private Integer genreDiversity;

    // 한 달 동안 들은 아티스트 수
    @Column(name = "artist_count", nullable = false)
    private Integer artistCount;

    // 평균 에너지
    @Column(name = "avg_energy", nullable = false)
    private Double avgEnergy;

    // 평균 발란스(감정 점수)??
    @Column(name = "avg_valence", nullable = false)
    private Double avgValence;

    // 가장 많이 들은 장르
    @Column(name = "top_genre", length = 50, nullable = false)
    private String topGenre;

    // 가장 많이 들은 아티스트
    @Column(name = "top_artist", length = 30, nullable = false)
    private String topArtist;

    // 고유 트랙 수 (중복 제외)
    @Column(name = "unique_tracks", nullable = false)
    private Integer uniqueTracks;

    // 희귀 곡 수 (ex: popularity < 30)
    @Column(name = "rare_tracks_count", nullable = false)
    private Integer rareTracksCount;

    // 통계 계산 시각
    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;
}
