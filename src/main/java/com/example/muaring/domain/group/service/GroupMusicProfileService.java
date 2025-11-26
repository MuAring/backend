package com.example.muaring.domain.group.service;

import com.example.muaring.domain.group.dto.GroupMusicProfileResponseDto;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupMusicProfile;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.repository.GroupMusicProfileRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.group.response.GroupException;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 그룹 음악 성향(GroupMusicProfile) 계산/조회 서비스.
 *
 * 컨셉 요약
 * ----------------------------------------------------------------
 * - 기준 기간: 최근 90일
 * - 기준 데이터: 그룹에 올라온 MusicPost (music + feature + genres 기반)
 * - 최소 유의미 곡 수: 10곡 (신뢰도 계산에 사용, 저장 로직에는 직접 관여 X)
 *
 * 메서드 역할
 * ----------------------------------------------------------------
 * 1) getOrRecalculateProfile(groupId)
 *    - 조회용
 *    - DB에 프로필이 있으면 그대로 사용
 *    - 없으면 내부적으로 새로 계산해서 생성 후 반환
 *    - 신뢰도(confidenceLevel)는 totalSongs 기준으로 계산해서 DTO에 포함
 *
 * 2) recalculateProfile(groupId)
 *    - 강제 재계산용 (관리자/배치/디버깅)
 *    - 항상 recalculateInternal()을 호출하여 새로 계산 후 덮어씀
 *
 * 3) recalculateInternal(groupId)
 *    - 실제 계산 로직이 모두 들어가는 private 메서드
 *    - 데이터 수집 → 피처/통계 계산 → GroupMusicProfile 업데이트
 *
 * 4) calculateConfidence(totalSongs)
 *    - totalSongs 기반 신뢰도 문자열(HIGH/LOW/NOT_AVAILABLE) 계산
 *    - 엔티티에 저장하지 않고 DTO/표현 레이어에서만 사용
 */
@Service
@RequiredArgsConstructor
public class GroupMusicProfileService {

    /** 그룹 성향 계산 기준 기간: 최근 90일 */
    private static final int GROUP_PROFILE_PERIOD_DAYS = 90;

    /**
     * "통계가 의미 있다"고 보는 최소 곡 수.
     * - 이 값은 신뢰도 계산에만 사용하고,
     *   실제 GroupMusicProfile 엔티티 저장 여부에는 직접 관여하지 않는다.
     */
    private static final int MIN_TRACKS_FOR_MEANINGFUL_STATS = 10;

    private final GroupRepository groupRepository;
    private final GroupMusicProfileRepository groupMusicProfileRepository;
    private final MusicPostRepository musicPostRepository;

    /**
     * 그룹 음악 프로필 조회 (없으면 계산해서 생성).
     * ----------------------------------------------------------------
     * - 클라이언트 기본 조회용 API에서 사용하는 메서드.
     * - DB에 이미 GroupMusicProfile이 존재하면 그대로 사용하고,
     *   없으면 recalculateInternal()을 호출하여 새로 생성.
     * - 항상 조회 시점에 totalSongs를 기준으로 신뢰도(confidenceLevel)를 계산하여
     *   응답 DTO에 포함해서 반환한다.
     *
     * 사용 예)
     * - GET /api/groups/{groupId}/music-profile
     */
    @Transactional
    public GroupMusicProfileResponseDto getOrRecalculateProfile(Long groupId) {
        GroupMusicProfile profile = groupMusicProfileRepository.findById(groupId)
                .orElseGet(() -> recalculateInternal(groupId));

        String confidence = calculateConfidence(profile.getTotalSongs());

        return GroupMusicProfileResponseDto.of(profile, confidence);
    }

    /**
     * 그룹 음악 프로필 강제 재계산.
     * ----------------------------------------------------------------
     * - 관리자/배치/운영툴 등에서 수동으로 최신화하고 싶을 때 사용.
     * - 기존 프로필 유무와 상관 없이 항상 recalculateInternal()을 호출하여
     *   최신 데이터 기준으로 다시 계산하고 덮어쓴다.
     *
     * 사용 예)
     * - POST /api/groups/{groupId}/music-profile/recalculate
     */
    @Transactional
    public GroupMusicProfileResponseDto recalculateProfile(Long groupId) {
        GroupMusicProfile profile = recalculateInternal(groupId);
        String confidence = calculateConfidence(profile.getTotalSongs());
        return GroupMusicProfileResponseDto.of(profile, confidence);
    }

    /**
     * 실제 그룹 음악 성향을 계산하는 핵심 메서드.
     * 외부에서는 직접 호출하지 않고, getOrRecalculateProfile / recalculateProfile 에서만 사용한다.
     *
     * 전체 흐름
     * ----------------------------------------------------------------
     * 1) 그룹 존재 여부 검증
     * 2) 기준 기간(최근 90일) 계산
     * 3) 기간 내 그룹 MusicPost 조회
     *      - music + musicFeature + musicGenre 까지 한 번에 조인하는 쿼리를 사용
     * 4) 포스트가 0개인 경우:
     *      - GroupMusicProfile을 NOT_AVAILABLE 상태로 초기화 후 저장
     * 5) 포스트가 1개 이상인 경우:
     *      - Music / Feature / Genre / 멤버 정보를 활용해 통계 계산
     *      - 평균 오디오 피처, 희귀도, 숨은 명곡 비율, 장르 다양성, 활성 멤버 비율, 등
     *      - 기존 프로필이 있으면 updateMetrics()로 업데이트
     *        없으면 createEmpty(group) 후 updateMetrics() 호출
     * 6) 최종적으로 저장된 GroupMusicProfile 엔티티를 반환
     */
    private GroupMusicProfile recalculateInternal(Long groupId) {
        // 1) 그룹 조회 및 존재 여부 검증
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        // 2) 기준 기간: 최근 90일
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(GROUP_PROFILE_PERIOD_DAYS);

        // 3) 최근 90일 그룹 포스트 조회 (music + feature + genres까지 fetch)
        //    - 구현 예시는 MusicPostRepository에서 findRecentGroupPostsWithFeatureAndGenres 메서드로 제공
        List<MusicPost> posts =
                musicPostRepository.findRecentGroupPostsWithFeatureAndGenres(groupId, from);

        // 4) 기간 내 포스트가 하나도 없는 경우 → NOT_AVAILABLE 상태로 초기화
        if (posts.isEmpty()) {
            GroupMusicProfile profile = groupMusicProfileRepository.findById(groupId)
                    .orElseGet(() -> GroupMusicProfile.createEmpty(group));

            // status = NOT_AVAILABLE, 모든 메트릭 null
            profile.resetToNotAvailable();
            return groupMusicProfileRepository.save(profile);
        }

        // 5) Music 리스트 추출 (중복 포함)
        List<Music> musics = posts.stream()
                .map(MusicPost::getMusic)
                .toList();

        // 6) 전체 곡 수 / 유니크 트랙 수
        int totalSongs = musics.size();
        int uniqueTracks = (int) musics.stream()
                .map(Music::getId)
                .distinct()
                .count();

        // 7) 장르 다양성 (genre_diversity)
        //    - 기준 기간 동안 등장한 모든 Music의 MusicGenre를 모아서
        //      distinct Genre ID 개수를 센다.
        Set<Long> genreIds = musics.stream()
                .flatMap(m -> m.getGenres().stream())      // MusicGenre 스트림
                .map(mg -> mg.getGenre().getId())          // Genre ID 추출
                .collect(Collectors.toSet());
        Integer genreDiversity = genreIds.isEmpty() ? null : genreIds.size();

        // 8) 활성 멤버 비율 (active_member_ratio)
        //    - 이 기간 동안 한 번이라도 포스트를 올린 멤버 수 / 그룹 전체 멤버 수
        long activeMemberCount = posts.stream()
                .map(mp -> mp.getMember().getId())
                .distinct()
                .count();

        Double activeMemberRatio = (group.getMemberCount() != null && group.getMemberCount() > 0)
                ? (double) activeMemberCount / group.getMemberCount()
                : null;

        // 9) 오디오 피처 관련 통계 (MusicFeature 기반)
        DoubleSummaryStatistics danceabilityStats = musics.stream()
                .map(Music::getFeature)
                .filter(f -> f != null)
                .mapToDouble(f -> f.getDanceability())
                .summaryStatistics();

        DoubleSummaryStatistics energyStats = musics.stream()
                .map(Music::getFeature)
                .filter(f -> f != null)
                .mapToDouble(f -> f.getEnergy())
                .summaryStatistics();

        DoubleSummaryStatistics valenceStats = musics.stream()
                .map(Music::getFeature)
                .filter(f -> f != null)
                .mapToDouble(f -> f.getValence())
                .summaryStatistics();

        DoubleSummaryStatistics acousticnessStats = musics.stream()
                .map(Music::getFeature)
                .filter(f -> f != null)
                .mapToDouble(f -> f.getAcousticness())
                .summaryStatistics();

        DoubleSummaryStatistics instrumentalnessStats = musics.stream()
                .map(Music::getFeature)
                .filter(f -> f != null)
                .mapToDouble(f -> f.getInstrumentalness())
                .summaryStatistics();

        DoubleSummaryStatistics speechinessStats = musics.stream()
                .map(Music::getFeature)
                .filter(f -> f != null)
                .mapToDouble(f -> f.getSpeechiness())
                .summaryStatistics();

        DoubleSummaryStatistics tempoStats = musics.stream()
                .map(Music::getFeature)
                .filter(f -> f != null)
                .mapToDouble(f -> f.getTempo())
                .summaryStatistics();

        DoubleSummaryStatistics loudnessStats = musics.stream()
                .map(Music::getFeature)
                .filter(f -> f != null)
                .mapToDouble(f -> f.getLoudness())
                .summaryStatistics();

        // 10) 인기 / 희귀도(popularity / rarity) 통계
        DoubleSummaryStatistics popularityStats = musics.stream()
                .mapToInt(Music::getPopularity)
                .asLongStream()
                .mapToDouble(v -> v)
                .summaryStatistics();

        // 희귀도(rarity): 단순히 (100 - popularity) 로 정의한 예시
        DoubleSummaryStatistics rarityStats = musics.stream()
                .mapToDouble(m -> 100 - m.getPopularity())
                .summaryStatistics();

        // 11) 각 피처 평균값 계산 (데이터 없으면 null)
        Double avgDanceability = safeAvg(danceabilityStats);
        Double avgEnergy = safeAvg(energyStats);
        Double avgValence = safeAvg(valenceStats);
        Double avgAcousticness = safeAvg(acousticnessStats);
        Double avgInstrumentalness = safeAvg(instrumentalnessStats);
        Double avgSpeechiness = safeAvg(speechinessStats);
        Double avgTempo = safeAvg(tempoStats);
        Double avgLoudness = safeAvg(loudnessStats);
        Double avgPopularity = safeAvg(popularityStats);
        Double avgRarity = safeAvg(rarityStats);

        // 12) 숨은 명곡 비율 (hidden_gem_ratio)
        //     - 예시로 popularity <= 30인 곡의 비율을 hidden gem 으로 취급
        long hiddenGemCount = musics.stream()
                .filter(m -> m.getPopularity() != null && m.getPopularity() <= 30)
                .count();

        Double hiddenGemRatio = totalSongs > 0
                ? (double) hiddenGemCount / totalSongs
                : null;

        // 13) 계산에 사용한 기간(일 수): 현재는 항상 90일 고정
        Integer calculatedPeriodDays = GROUP_PROFILE_PERIOD_DAYS;

        // 14) 기존 프로필이 있으면 재활용, 없으면 빈 프로필 생성 후 업데이트
        GroupMusicProfile profile = groupMusicProfileRepository.findById(groupId)
                .orElseGet(() -> GroupMusicProfile.createEmpty(group));

        // updateMetrics 내부에서 status를 READY로 설정하며,
        // @PreUpdate 로직이 상태와 메트릭의 일관성을 검증한다.
        profile.updateMetrics(
                avgDanceability, avgEnergy, avgValence,
                avgAcousticness, avgInstrumentalness, avgSpeechiness,
                avgTempo, avgLoudness, avgPopularity, avgRarity,
                hiddenGemRatio,
                totalSongs, uniqueTracks, genreDiversity,
                activeMemberRatio, calculatedPeriodDays
        );

        return groupMusicProfileRepository.save(profile);
    }

    /**
     * DoubleSummaryStatistics에서 안전하게 평균값을 꺼내는 헬퍼 메서드.
     * - count == 0 인 경우 null 반환
     */
    private Double safeAvg(DoubleSummaryStatistics stats) {
        return stats.getCount() > 0 ? stats.getAverage() : null;
    }

    /**
     * 곡 개수를 기반으로 신뢰도(confidenceLevel)를 계산한다.
     *
     * 규칙
     * ----------------------------------------------------------------
     * - totalSongs == null 또는 0  → "NOT_AVAILABLE"
     * - 1 <= totalSongs < MIN_TRACKS_FOR_MEANINGFUL_STATS (10) → "LOW"
     * - totalSongs >= MIN_TRACKS_FOR_MEANINGFUL_STATS → "HIGH"
     *
     * 이 값은 DB에 저장되지 않고, ResponseDto 레벨에서만 사용된다.
     */
    private String calculateConfidence(Integer totalSongs) {
        if (totalSongs == null || totalSongs == 0) {
            return "NOT_AVAILABLE";
        }
        if (totalSongs < MIN_TRACKS_FOR_MEANINGFUL_STATS) {
            return "LOW";
        }
        return "HIGH";
    }
}
