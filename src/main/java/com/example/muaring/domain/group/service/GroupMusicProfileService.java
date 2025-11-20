package com.example.muaring.domain.group.service;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupMusicProfile;
import com.example.muaring.domain.group.repository.GroupMusicProfileRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMusicProfileService {

    private static final int GROUP_PROFILE_PERIOD_DAYS = 90;
    private static final int MIN_TRACKS_FOR_MEANINGFUL_STATS = 10;

    private final GroupRepository groupRepository;
    private final GroupMusicProfileRepository groupMusicProfileRepository;
    private final MusicPostRepository musicPostRepository;

    /**
     * 특정 그룹의 음악 성향을 재계산
     * - 기간: 최근 90일
     * - 데이터 0개면 NOT_AVAILABLE
     * - 1~9개면: 계산은 하지만 "데이터가 적다"는 신뢰도를 따로 표기 (DTO/추천 쪽에서)
     */
    @Transactional
    public GroupMusicProfile recalculateGroupProfile(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹을 찾을 수 없습니다. id=" + groupId));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusDays(GROUP_PROFILE_PERIOD_DAYS);

        // 1) 최근 90일 그룹 포스트 + music.feature 까지 로드
        List<MusicPost> posts = musicPostRepository.findRecentGroupPostsWithFeature(groupId, from);

        if (posts.isEmpty()) {
            // 데이터 없으면 NOT_AVAILABLE로 초기화
            GroupMusicProfile profile = groupMusicProfileRepository.findById(groupId)
                    .orElseGet(() -> GroupMusicProfile.createEmpty(group));
            profile.resetToNotAvailable();
            return groupMusicProfileRepository.save(profile);
        }

        // 2) Music 리스트/Feature/Popularity 추출
        List<Music> musics = posts.stream()
                .map(MusicPost::getMusic)
                .collect(Collectors.toList());

        int totalSongs = musics.size();
        int uniqueTracks = (int) musics.stream()
                .map(Music::getId)
                .distinct()
                .count();

        // 장르 다양성: musicId 기준 distinct genre 수 (MusicGenre 엔티티 있으면 여기서 계산 가능)
        // 일단은 0 또는 null로 두고, 추후 MusicGenre 연동 시 채우자.
        Integer genreDiversity = null;

        // 활동 멤버 비율: 그룹 내 실제 활성 멤버 수 / 전체 멤버 수
        // 여기서는 간단히 "이 기간 동안 포스트를 올린 distinct 멤버 수 / group.memberCount" 로 계산
        long activeMemberCount = posts.stream()
                .map(mp -> mp.getMember().getId())
                .distinct()
                .count();
        Double activeMemberRatio = group.getMemberCount() != null && group.getMemberCount() > 0
                ? (double) activeMemberCount / group.getMemberCount()
                : null;

        // 희귀도/평균 계산: MusicFeature + popularity 기반
        DoubleSummaryStatistics danceabilityStats = musics.stream()
                .map(m -> m.getFeature())
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

        DoubleSummaryStatistics popularityStats = musics.stream()
                .mapToInt(Music::getPopularity)
                .asLongStream()
                .mapToDouble(v -> v)
                .summaryStatistics();

        // 희귀도(rarity): 예시로 (100 - popularity) 사용
        DoubleSummaryStatistics rarityStats = musics.stream()
                .mapToDouble(m -> 100 - m.getPopularity())
                .summaryStatistics();

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

        // 숨은 명곡 비율: popularity <= 30 으로 간단 정의
        long hiddenGemCount = musics.stream()
                .filter(m -> m.getPopularity() != null && m.getPopularity() <= 30)
                .count();
        Double hiddenGemRatio = totalSongs > 0
                ? (double) hiddenGemCount / totalSongs
                : null;

        // calculatedPeriodDays: 항상 90으로 고정 (최근 90일 기준 프로필)
        Integer calculatedPeriodDays = GROUP_PROFILE_PERIOD_DAYS;

        // 3) 기존 프로필 있으면 업데이트, 없으면 생성
        GroupMusicProfile profile = groupMusicProfileRepository.findById(groupId)
                .orElseGet(() -> GroupMusicProfile.createEmpty(group));

        profile.updateMetrics(
                avgDanceability, avgEnergy, avgValence,
                avgAcousticness, avgInstrumentalness, avgSpeechiness,
                avgTempo, avgLoudness, avgPopularity, avgRarity,
                hiddenGemRatio,
                totalSongs, uniqueTracks, genreDiversity,
                activeMemberRatio, calculatedPeriodDays
        );

        // 여기서 totalSongs 기준으로 신뢰도 HIGH/MEDIUM/LOW 분류는 별도의 DTO나 추천 로직에서 사용
        // (예: totalSongs >= 10이면 "의미 있는 통계"로 간주)

        return groupMusicProfileRepository.save(profile);
    }

    private Double safeAvg(DoubleSummaryStatistics stats) {
        return stats.getCount() > 0 ? stats.getAverage() : null;
    }
}
