package com.example.muaring.domain.group.entity;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.response.GroupException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_music_profile")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMusicProfile {

    @Id
    @Column(name = "group_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "group_id")
    private Group group;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProfileStatus status;

    @Column(name = "avg_danceability")
    private Double avgDanceability;

    @Column(name = "avg_energy")
    private Double avgEnergy;

    @Column(name = "avg_valence")
    private Double avgValence;

    @Column(name = "avg_acousticness")
    private Double avgAcousticness;

    @Column(name = "avg_instrumentalness")
    private Double avgInstrumentalness;

    @Column(name = "avg_speechiness")
    private Double avgSpeechiness;

    @Column(name = "avg_tempo")
    private Double avgTempo;

    @Column(name = "avg_loudness")
    private Double avgLoudness;

    @Column(name = "avg_popularity")
    private Double avgPopularity;

    @Column(name = "avg_rarity")
    private Double avgRarity;

    @Column(name = "hidden_gem_ratio")
    private Double hiddenGemRatio;

    @Column(name = "total_songs")
    private Integer totalSongs;

    @Column(name = "unique_tracks")
    private Integer uniqueTracks;

    @Column(name = "genre_diversity")
    private Integer genreDiversity;

    @Column(name = "active_member_ratio")
    private Double activeMemberRatio;

    @Column(name = "caculated_period_days")
    private Integer calculatedPeriodDays;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 그룹 생성 시 기본 비어있는 profile 생성 *
    public static GroupMusicProfile createEmpty(Group group) {
        GroupMusicProfile profile = new GroupMusicProfile();
        profile.group = group;
        profile.status = ProfileStatus.NOT_AVAILABLE;
        profile.createdAt = LocalDateTime.now();
        profile.updatedAt = LocalDateTime.now();
        return profile;
    }

    // 배치 완료 후 READY 상태로 만드는 팩토리 */
    public static GroupMusicProfile of(
            Group group,
            Double avgDanceability, Double avgEnergy, Double avgValence,
            Double avgAcousticness, Double avgInstrumentalness, Double avgSpeechiness,
            Double avgTempo, Double avgLoudness, Double avgPopularity, Double avgRarity,
            Double hiddenGemRatio,
            Integer totalSongs, Integer uniqueTracks, Integer genreDiversity,
            Double activeMemberRatio, Integer calculatedPeriodDays
    ) {
        GroupMusicProfile profile = new GroupMusicProfile();
        profile.group = group;
        profile.status = ProfileStatus.READY;
        profile.avgDanceability = avgDanceability;
        profile.avgEnergy = avgEnergy;
        profile.avgValence = avgValence;
        profile.avgAcousticness = avgAcousticness;
        profile.avgInstrumentalness = avgInstrumentalness;
        profile.avgSpeechiness = avgSpeechiness;
        profile.avgTempo = avgTempo;
        profile.avgLoudness = avgLoudness;
        profile.avgPopularity = avgPopularity;
        profile.avgRarity = avgRarity;
        profile.hiddenGemRatio = hiddenGemRatio;
        profile.totalSongs = totalSongs;
        profile.uniqueTracks = uniqueTracks;
        profile.genreDiversity = genreDiversity;
        profile.activeMemberRatio = activeMemberRatio;
        profile.calculatedPeriodDays = calculatedPeriodDays;
        profile.createdAt = LocalDateTime.now();
        profile.updatedAt = LocalDateTime.now();
        return profile;
    }

    /* 무결성: READY일 때는 주요 메트릭이 null이면 안 되고,
   NOT_AVAILABLE/PROCESSING일 땐 메트릭이 있으면 안 됨 */
    @PrePersist @PreUpdate
    private void validateConsistency() {
        boolean hasMetrics =
                avgDanceability != null || avgEnergy != null || avgValence != null ||
                        avgAcousticness != null || avgInstrumentalness != null || avgSpeechiness != null ||
                        avgTempo != null || avgLoudness != null || avgPopularity != null ||
                        avgRarity != null || hiddenGemRatio != null || totalSongs != null ||
                        uniqueTracks != null || genreDiversity != null || activeMemberRatio != null ||
                        calculatedPeriodDays != null;

        switch (status) {
            case NOT_AVAILABLE, PROCESSING -> {
                if (hasMetrics) throw new GroupException(GroupErrorCode.METRICS_CONFLICT);
            }
            case READY -> {
                // 핵심 메트릭 필수
                if (avgDanceability == null || avgEnergy == null || avgValence == null) {
                    throw new GroupException(GroupErrorCode.METRICS_NOT_FOUND);
                }
                // 선택: 안전 범위 체크
                if (activeMemberRatio != null && (activeMemberRatio < 0.0 || activeMemberRatio > 1.0)) {
                    throw new GroupException(GroupErrorCode.METRICS_CONFLICT);
                }
                if ((totalSongs != null && totalSongs < 0) ||
                        (uniqueTracks != null && uniqueTracks < 0) ||
                        (genreDiversity != null && genreDiversity < 0) ||
                        (calculatedPeriodDays != null && calculatedPeriodDays < 0)) {
                    throw new GroupException(GroupErrorCode.METRICS_CONFLICT);
                }
            }
        }
    }

}
