package com.example.muaring.domain.member.entity;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.response.MemberErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 음악 성향 프로필 (MemberMusicProfile)
 * -----------------------------------------------
 * - Member : 1 : 1 매핑 (PK 공유)
 * - status:
 *      NOT_AVAILABLE → 데이터 부족
 *      PROCESSING    → 계산 중 (확장성 대비)
 *      READY         → 정상 계산 완료
 * - GroupMusicProfile과 동일한 구조/패턴 유지
 */
@Entity
@Table(name = "member_music_profile")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMusicProfile {

    @Id
    @Column(name = "member_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
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

    @Column(name = "calculated_days")
    private Integer calculatedDays;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 최초 생성 시 기본값 (NOT_AVAILABLE)
     */
    public static MemberMusicProfile createEmpty(Member member) {
        MemberMusicProfile profile = new MemberMusicProfile();
        profile.member = member;
        profile.status = ProfileStatus.NOT_AVAILABLE;
        return profile;
    }

    /**
     * 계산 완료 후 READY 상태로 만드는 팩토리
     */
    public static MemberMusicProfile of(
            Member member,
            Double avgDanceability, Double avgEnergy, Double avgValence,
            Double avgAcousticness, Double avgInstrumentalness, Double avgSpeechiness,
            Double avgTempo, Double avgLoudness, Double avgPopularity, Double avgRarity,
            Double hiddenGemRatio, Integer calculatedDays
    ) {
        MemberMusicProfile profile = new MemberMusicProfile();
        profile.member = member;
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
        profile.calculatedDays = calculatedDays;
        return profile;
    }

    /**
     * 기존 프로필 업데이트용 (GroupMusicProfile과 동일)
     */
    public void updateMetrics(
            Double avgDanceability, Double avgEnergy, Double avgValence,
            Double avgAcousticness, Double avgInstrumentalness, Double avgSpeechiness,
            Double avgTempo, Double avgLoudness, Double avgPopularity, Double avgRarity,
            Double hiddenGemRatio,
            Integer calculatedDays
    ) {
        this.status = ProfileStatus.READY;
        this.avgDanceability = avgDanceability;
        this.avgEnergy = avgEnergy;
        this.avgValence = avgValence;
        this.avgAcousticness = avgAcousticness;
        this.avgInstrumentalness = avgInstrumentalness;
        this.avgSpeechiness = avgSpeechiness;
        this.avgTempo = avgTempo;
        this.avgLoudness = avgLoudness;
        this.avgPopularity = avgPopularity;
        this.avgRarity = avgRarity;
        this.hiddenGemRatio = hiddenGemRatio;
        this.calculatedDays = calculatedDays;
    }

    /**
     * NOT_AVAILABLE로 초기화 (데이터 부족 시)
     */
    public void resetToNotAvailable() {
        this.status = ProfileStatus.NOT_AVAILABLE;
        this.avgDanceability = null;
        this.avgEnergy = null;
        this.avgValence = null;
        this.avgAcousticness = null;
        this.avgInstrumentalness = null;
        this.avgSpeechiness = null;
        this.avgTempo = null;
        this.avgLoudness = null;
        this.avgPopularity = null;
        this.avgRarity = null;
        this.hiddenGemRatio = null;
        this.calculatedDays = null;
    }

    /**
     * 상태/필드 무결성 보장 — GroupMusicProfile 패턴과 통일
     */
    @PrePersist
    void ensureDefaults() {
        if (status == null) status = ProfileStatus.NOT_AVAILABLE;
    }

    @PreUpdate
    private void validateConsistency() {
        boolean hasMetrics =
                avgDanceability != null || avgEnergy != null || avgValence != null ||
                        avgAcousticness != null || avgInstrumentalness != null || avgSpeechiness != null ||
                        avgTempo != null || avgLoudness != null || avgPopularity != null ||
                        avgRarity != null || hiddenGemRatio != null || calculatedDays != null;

        switch (status) {
            case NOT_AVAILABLE, PROCESSING -> {
                if (hasMetrics) {
                    throw new MemberException(MemberErrorCode.METRICS_CONFLICT);
                }
            }
            case READY -> {
                if (avgDanceability == null || avgEnergy == null || avgValence == null) {
                    throw new MemberException(MemberErrorCode.METRICS_NOT_FOUND);
                }
            }
        }
    }

    private double[] buildAudioVector(MemberMusicProfile profile) {
        return new double[] {
                nullSafe(profile.getAvgDanceability()),
                nullSafe(profile.getAvgEnergy()),
                nullSafe(profile.getAvgValence()),
                nullSafe(profile.getAvgAcousticness()),
                nullSafe(profile.getAvgInstrumentalness()),
                nullSafe(profile.getAvgSpeechiness()),
                nullSafe(profile.getAvgTempo()),
                nullSafe(profile.getAvgLoudness()),
                nullSafe(profile.getAvgPopularity())
        };
    }

    private double nullSafe(Double value) {
        return value == null ? 0.0 : value;
    }

}
