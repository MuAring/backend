package com.example.muaring.domain.member.dto.response;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.member.entity.MemberMusicProfile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MemberMusicProfileResponseDto {

    private Long memberId;
    private String nickname;
    private ProfileStatus status;
    private String confidenceLevel;

    private Double avgDanceability;
    private Double avgEnergy;
    private Double avgValence;
    private Double avgAcousticness;
    private Double avgInstrumentalness;
    private Double avgSpeechiness;
    private Double avgTempo;
    private Double avgLoudness;
    private Double avgPopularity;
    private Double avgRarity;
    private Double hiddenGemRatio;

    private Integer calculatedDays;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MemberMusicProfileResponseDto of(MemberMusicProfile profile, String confidence) {
        return MemberMusicProfileResponseDto.builder()
                .memberId(profile.getMember().getId())
                .nickname(profile.getMember().getNickname())
                .status(profile.getStatus())
                .confidenceLevel(confidence)
                .avgDanceability(profile.getAvgDanceability())
                .avgEnergy(profile.getAvgEnergy())
                .avgValence(profile.getAvgValence())
                .avgAcousticness(profile.getAvgAcousticness())
                .avgInstrumentalness(profile.getAvgInstrumentalness())
                .avgSpeechiness(profile.getAvgSpeechiness())
                .avgTempo(profile.getAvgTempo())
                .avgLoudness(profile.getAvgLoudness())
                .avgPopularity(profile.getAvgPopularity())
                .avgRarity(profile.getAvgRarity())
                .hiddenGemRatio(profile.getHiddenGemRatio())
                .calculatedDays(profile.getCalculatedDays())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
