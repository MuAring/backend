package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupCategory;
import com.example.muaring.domain.group.entity.GroupMusicProfile;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetailResponseDto {

    private Long groupId;
    private Long adminUserId;
    private String name;
    private String description;
    private Integer memberCount;
    private Integer maxMembers;
    private Boolean isPublic;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime playlistUpdatedAt;

    private List<CategoryDto> category;

    private MusicProfileDto musicProfile;

    // ---------- Factories ----------

    /** 기본(라이트): status, updatedAt만 포함 */
    public static GroupDetailResponseDto ofLight(
            Group group,
            int memberCount,
            List<GroupCategory> categories,
            GroupMusicProfile profile // null 가능
    ) {
        return baseBuilder(group, memberCount, categories)
                .musicProfile(MusicProfileDto.ofHeader(profile))
                .build();
    }

    /** 확장: metrics까지 포함 */
    public static GroupDetailResponseDto ofWithMetrics(
            Group group,
            int memberCount,
            List<GroupCategory> categories,
            GroupMusicProfile profile // null 가능
    ) {
        return baseBuilder(group, memberCount, categories)
                .musicProfile(MusicProfileDto.ofFull(profile))
                .build();
    }

    // ---------- Internals ----------

    private static GroupDetailResponseDtoBuilder baseBuilder(
            Group group,
            int memberCount,
            List<GroupCategory> categories
    ) {
        return GroupDetailResponseDto.builder()
                .groupId(group.getId())
                .adminUserId(group.getAdmin().getId())
                .name(group.getName())
                .description(group.getDescription())
                .memberCount(memberCount)
                .maxMembers(group.getMaxMembers())
                .isPublic(group.getIsPublic())
                .isDeleted(group.getIsDeleted())
                .createdAt(group.getCreatedAt())
                .playlistUpdatedAt(group.getPlaylistUpdatedAt())
                .category(toCategoryDtos(categories));
    }

    private static List<CategoryDto> toCategoryDtos(List<GroupCategory> categories) {
        return categories == null ? List.of() :
                categories.stream()
                        .filter(Objects::nonNull)
                        .map(c -> new CategoryDto(c.getId(), c.getName()))
                        .collect(Collectors.toList());
    }

    // ---------- Nested DTOs ----------

    @Getter
    @AllArgsConstructor
    public static class CategoryDto {
        private Long groupCategoryId;
        private String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MusicProfileDto {
        private ProfileStatus status;          // NOT_AVAILABLE | PROCESSING | READY
        private LocalDateTime updatedAt;       // 최신성 배지용
        private Metrics metrics;               // ofFull에서만 채움 (라이트에선 null)

        public static MusicProfileDto ofHeader(GroupMusicProfile p) {
            if (p == null) {
                return MusicProfileDto.builder()
                        .status(ProfileStatus.NOT_AVAILABLE)
                        .updatedAt(null)
                        .metrics(null)
                        .build();
            }
            return MusicProfileDto.builder()
                    .status(p.getStatus())
                    .updatedAt(p.getUpdatedAt())
                    .metrics(null) // header only
                    .build();
        }

        public static MusicProfileDto ofFull(GroupMusicProfile p) {
            if (p == null) {
                return ofHeader(null);
            }
            return MusicProfileDto.builder()
                    .status(p.getStatus())
                    .updatedAt(p.getUpdatedAt())
                    .metrics(Metrics.from(p))
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Metrics {
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

        public static Metrics from(GroupMusicProfile p) {
            if (p == null || p.getStatus() != ProfileStatus.READY) return null;
            return Metrics.builder()
                    .avgDanceability(p.getAvgDanceability())
                    .avgEnergy(p.getAvgEnergy())
                    .avgValence(p.getAvgValence())
                    .avgAcousticness(p.getAvgAcousticness())
                    .avgInstrumentalness(p.getAvgInstrumentalness())
                    .avgSpeechiness(p.getAvgSpeechiness())
                    .avgTempo(p.getAvgTempo())
                    .avgLoudness(p.getAvgLoudness())
                    .avgPopularity(p.getAvgPopularity())
                    .avgRarity(p.getAvgRarity())
                    .hiddenGemRatio(p.getHiddenGemRatio())
                    .totalSongs(p.getTotalSongs())
                    .uniqueTracks(p.getUniqueTracks())
                    .genreDiversity(p.getGenreDiversity())
                    .activeMemberRatio(p.getActiveMemberRatio())
                    .calculatedPeriodDays(p.getCalculatedPeriodDays())
                    .build();
        }
    }
}
