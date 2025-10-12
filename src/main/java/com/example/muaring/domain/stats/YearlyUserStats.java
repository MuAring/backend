package com.example.muaring.domain.stats;

import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "yearly_user_stats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "year"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YearlyUserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long statId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 연도 (ex: 2025)
    @Column(name = "year", nullable = false)
    private Integer year;

    // 총 감상 곡 수
    @Column(name = "total_songs", nullable = false)
    private Integer totalSongs;

    // 참여율 (0.0 ~ 1.0)
    @Column(name = "completion_rate", nullable = false)
    private Double completionRate;

    // 최장 연속 감상일 수
    @Column(name = "consecutive_days", nullable = false)
    private Integer consecutiveDays;

    // 한 해 동안 들은 장르 수
    @Column(name = "total_genre", nullable = false)
    private Integer totalGenre;

    // 한 해 동안 들은 아티스트 수
    @Column(name = "total_artist", nullable = false)
    private Integer totalArtist;

    // 에너지 트렌드 (증가 / 감소 / 유지)
    @Enumerated(EnumType.STRING)
    @Column(name = "energy_trend", nullable = false, length = 10)
    private EnergyTrend energyTrend;

    // 무드 변화 (밝아짐 / 어두워짐)
    @Enumerated(EnumType.STRING)
    @Column(name = "mood_shift", nullable = false, length = 10)
    private MoodShift moodShift;

    // 계절별 선호 장르 (JSON 문자열로 저장)
    @Column(name = "top_season_genre", columnDefinition = "JSON", nullable = false)
    private String topSeasonGenre;

    // 통계 계산 시간
    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public enum EnergyTrend {
        증가, 감소, 유지
    }

    public enum MoodShift {
        밝아짐, 어두워짐
    }
}
