package com.example.muaring.domain.member.service;

import com.example.muaring.domain.member.dto.response.MemberMusicProfileResponseDto;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.entity.MemberMusicProfile;
import com.example.muaring.domain.member.response.MemberErrorCode;
import com.example.muaring.domain.member.repository.MemberMusicProfileRepository;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;

/**
 * 사용자 음악 성향(UserMusicProfile) 계산/조회 서비스.
 *
 * 정책 요약
 * -------------------------------------------------------
 * 1) 최근 30일 내 10곡 이상 → HIGH
 * 2) 부족하면 최근 180일로 확장 → 10곡 이상 → MEDIUM
 * 3) 그래도 10곡 미만이면 LOW
 * 4) 0곡이면 NOT_AVAILABLE
 * 5) 최대 곡 수: 최신 60곡까지만 사용
 */
@Service
@RequiredArgsConstructor
public class MemberMusicProfileService {

    private static final int PRIMARY_PERIOD_DAYS = 30;
    private static final int EXTENDED_PERIOD_DAYS = 180;
    private static final int MIN_TRACKS = 10;
    private static final int MAX_TRACKS = 60;

    private final MemberRepository memberRepository;
    private final MemberMusicProfileRepository memberMusicProfileRepository;
    private final MusicPostRepository musicPostRepository;

    /**
     * 사용자 성향 조회 (없으면 자동 계산해서 생성)
     */
    @Transactional
    public MemberMusicProfileResponseDto getOrRecalculate(Long memberId) {

        MemberMusicProfile profile = memberMusicProfileRepository.findById(memberId)
                .orElseGet(() -> recalcInternal(memberId));

        String confidence = calculateConfidence(profile.getCalculatedDays());
        return MemberMusicProfileResponseDto.of(profile, confidence);
    }

    /**
     * 강제 재계산
     */
    @Transactional
    public MemberMusicProfileResponseDto recalculate(Long memberId) {
        MemberMusicProfile profile = recalcInternal(memberId);
        String confidence = calculateConfidence(profile.getCalculatedDays());
        return MemberMusicProfileResponseDto.of(profile, confidence);
    }

    /**
     * 내부 계산 로직
     */
    private MemberMusicProfile recalcInternal(Long memberId) {

        // 1. 사용자 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        // 2. 1차 기간: 최근 30일
        List<MusicPost> posts30 = musicPostRepository.findRecentUserPostsWithFeatureAndGenres(
                memberId,
                now.minusDays(PRIMARY_PERIOD_DAYS)
        );

        List<MusicPost> selectedPosts;

        // 3. 데이터 충분하면 바로 사용
        if (posts30.size() >= MIN_TRACKS) {
            selectedPosts = posts30;
        }
        // 4. 부족하면 180일 확장
        else {
            List<MusicPost> posts180 = musicPostRepository.findRecentUserPostsWithFeatureAndGenres(
                    memberId,
                    now.minusDays(EXTENDED_PERIOD_DAYS)
            );

            selectedPosts = posts180.isEmpty() ? List.of() : posts180;
        }

        // 5. 0곡이면 NOT_AVAILABLE
        if (selectedPosts.isEmpty()) {
            MemberMusicProfile profile = memberMusicProfileRepository.findById(memberId)
                    .orElseGet(() -> MemberMusicProfile.createEmpty(member));
            profile.resetToNotAvailable();
            return memberMusicProfileRepository.save(profile);
        }

        // 6. 최대 60곡 제한 (최신순)
        selectedPosts = selectedPosts.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(MAX_TRACKS)
                .toList();

        // 7. Music 리스트
        List<Music> musics = selectedPosts.stream()
                .map(MusicPost::getMusic)
                .toList();

        int calculatedDays = musics.size();

        // 8. 평균값/희귀도/피처 계산은 Group 버전과 동일
        Double avgDanceability = safeAvg(musics.stream().map(Music::getFeature).mapToDouble(f -> f.getDanceability()).summaryStatistics());
        Double avgEnergy = safeAvg(musics.stream().map(Music::getFeature).mapToDouble(f -> f.getEnergy()).summaryStatistics());
        Double avgValence = safeAvg(musics.stream().map(Music::getFeature).mapToDouble(f -> f.getValence()).summaryStatistics());
        Double avgAcousticness = safeAvg(musics.stream().map(Music::getFeature).mapToDouble(f -> f.getAcousticness()).summaryStatistics());
        Double avgInstrumentalness = safeAvg(musics.stream().map(Music::getFeature).mapToDouble(f -> f.getInstrumentalness()).summaryStatistics());
        Double avgSpeechiness = safeAvg(musics.stream().map(Music::getFeature).mapToDouble(f -> f.getSpeechiness()).summaryStatistics());
        Double avgTempo = safeAvg(musics.stream().map(Music::getFeature).mapToDouble(f -> f.getTempo()).summaryStatistics());
        Double avgLoudness = safeAvg(musics.stream().map(Music::getFeature).mapToDouble(f -> f.getLoudness()).summaryStatistics());
        Double avgPopularity = safeAvg(musics.stream().mapToInt(Music::getPopularity).asLongStream().mapToDouble(v -> v).summaryStatistics());
        Double avgRarity = safeAvg(musics.stream().mapToDouble(m -> 100 - m.getPopularity()).summaryStatistics());

        long hiddenGemCount = musics.stream()
                .filter(m -> m.getPopularity() != null && m.getPopularity() <= 30)
                .count();
        Double hiddenGemRatio = calculatedDays > 0
                ? (double) hiddenGemCount / calculatedDays
                : null;

        // 9. 기존 프로필 로딩 or 초기 생성
        MemberMusicProfile profile = memberMusicProfileRepository.findById(memberId)
                .orElseGet(() -> MemberMusicProfile.createEmpty(member));

        // 10. 업데이트
        profile.updateMetrics(
                avgDanceability, avgEnergy, avgValence,
                avgAcousticness, avgInstrumentalness, avgSpeechiness,
                avgTempo, avgLoudness, avgPopularity, avgRarity,
                hiddenGemRatio,
                calculatedDays
        );

        return memberMusicProfileRepository.save(profile);
    }

    /** 평균 안전 계산 */
    private Double safeAvg(DoubleSummaryStatistics stats) {
        return stats.getCount() > 0 ? stats.getAverage() : null;
    }

    /** 신뢰도 계산 */
    private String calculateConfidence(Integer days) {
        if (days == null || days == 0) return "NOT_AVAILABLE";
        if (days < MIN_TRACKS) return "LOW";
        return "HIGH"; // 30일 기준 HIGH or 180일 MEDIUM은 필요 시 여기서 조정 가능
    }
}
