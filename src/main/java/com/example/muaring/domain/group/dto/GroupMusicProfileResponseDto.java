package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.group.entity.GroupMusicProfile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupMusicProfileResponseDto {

    private Long groupId;
    private String groupName;

    private ProfileStatus status;      // NOT_AVAILABLE / READY 등
    private String confidenceLevel;    // "HIGH" / "MEDIUM" / "LOW" / "NOT_AVAILABLE"

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

    private Integer totalSongs;
    private Integer uniqueTracks;
    private Integer genreDiversity;

    private Double activeMemberRatio;
    private Integer calculatedPeriodDays;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GroupMusicProfileResponseDto of(
            GroupMusicProfile profile,
            String confidenceLevel
    ) {
        return GroupMusicProfileResponseDto.builder()
                .groupId(profile.getGroup().getId())
                .groupName(profile.getGroup().getName())
                .status(profile.getStatus())
                .confidenceLevel(confidenceLevel)
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
                .totalSongs(profile.getTotalSongs())
                .uniqueTracks(profile.getUniqueTracks())
                .genreDiversity(profile.getGenreDiversity())
                .activeMemberRatio(profile.getActiveMemberRatio())
                .calculatedPeriodDays(profile.getCalculatedPeriodDays())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
