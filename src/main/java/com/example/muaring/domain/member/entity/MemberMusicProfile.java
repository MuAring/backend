package com.example.muaring.domain.member.entity;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.response.MemberErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

// 사용자 음악 취향 수치화한 테이블
@Entity
@Table(name = "member_music_profile")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProfileStatus status;

    // 새 멤버 생성 시 기본 profile (비어 있음)
    public static MemberMusicProfile createEmpty(Member member) {
        MemberMusicProfile profile = new MemberMusicProfile();
        profile.member = member;
        profile.status = ProfileStatus.NOT_AVAILABLE;
        return profile;
    }

    // 배치 계산 완료 후 READY 상태로 만드는 팩토리
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

    // 생성/수정 공통 안전장치: status 기본값
    @PrePersist
    void ensureDefaults() {
        if (status == null) status = ProfileStatus.NOT_AVAILABLE;
    }

    /* 무결성: READY일 때는 주요 메트릭이 null이면 안 되고,
       NOT_AVAILABLE/PROCESSING일 땐 메트릭이 있으면 안 됨 */
    @PreUpdate
    private void validateConsistency() {
        boolean hasMetrics =
                avgDanceability != null || avgEnergy != null || avgValence != null ||
                        avgAcousticness != null || avgInstrumentalness != null || avgSpeechiness != null ||
                        avgTempo != null || avgLoudness != null || avgPopularity != null ||
                        avgRarity != null || hiddenGemRatio != null || calculatedDays != null;

        switch (status) {
            case NOT_AVAILABLE, PROCESSING -> {
                if (hasMetrics) throw new MemberException(MemberErrorCode.METRICS_CONFLICT);
            }
            case READY -> {
                // 필요 시 핵심 필드 몇 개는 반드시 채워지도록 최소 요건 체크
                if (avgDanceability == null || avgEnergy == null || avgValence == null) {
                    throw new MemberException(MemberErrorCode.METRICS_NOT_FOUND);
                }
            }
        }
    }
}
