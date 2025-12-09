package com.example.muaring.domain.recommendation.service;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.entity.MemberMusicProfile;
import com.example.muaring.domain.member.repository.FollowRepository;
import com.example.muaring.domain.member.repository.MemberMusicProfileRepository;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.service.MemberService;
import com.example.muaring.domain.recommendation.dto.MemberRecommendItemDto;
import com.example.muaring.domain.recommendation.entity.MemberRecommendation;
import com.example.muaring.domain.recommendation.entity.SimilarityCache;
import com.example.muaring.domain.recommendation.model.SimilarityScoreDetail;
import com.example.muaring.domain.recommendation.repository.MemberRecommendationRepository;
import com.example.muaring.domain.recommendation.repository.SimilarityCacheRepository;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.repository.MusicPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRecommendationService {

    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final MemberMusicProfileRepository memberMusicProfileRepository;
    private final SimilarityCacheRepository similarityCacheRepository;
    private final FollowRepository followRepository;
    private final MusicPostRepository musicPostRepository;
    private final MemberRecommendationRepository memberRecommendationRepository;

    // =========================================================================
    // 1. 멤버 ↔ 멤버 유사도 계산
    // =========================================================================

    /**
     * 특정 멤버에 대해, 다른 모든 멤버와의 유사도 재계산
     * - similarity_cache 테이블에 저장
     */
    @Transactional
    public void recalcSimilaritiesForMember(Long memberId) {

        MemberMusicProfile targetProfile = memberMusicProfileRepository.findById(memberId)
                .orElse(null);

        if (targetProfile == null || targetProfile.getStatus() != ProfileStatus.READY) {
            // 프로필 없거나 계산 준비 안된 경우: 관련 캐시 삭제 후 종료
            similarityCacheRepository.deleteAllByMemberId(memberId);
            return;
        }

        Member targetMember = targetProfile.getMember();

        // 1) 기존 캐시 삭제
        similarityCacheRepository.deleteAllByMemberId(memberId);

        // 2) READY 상태의 다른 멤버 프로필들 조회
        List<MemberMusicProfile> profiles =
                memberMusicProfileRepository.findByStatus(ProfileStatus.READY);

        LocalDateTime now = LocalDateTime.now();

        for (MemberMusicProfile otherProfile : profiles) {
            Member otherMember = otherProfile.getMember();
            Long otherId = otherMember.getId();

            if (otherId.equals(memberId)) continue; // 자기 자신은 스킵

            try {
                SimilarityScoreDetail scoreDetail =
                        calculateMemberSimilarityScore(targetProfile, otherProfile);

                if (scoreDetail.getTotalScore() <= 0.0) continue;

                upsertSimilarity(targetMember, otherMember, scoreDetail, now);
            } catch (Exception e) {
                // 한 명 계산 실패했다고 전체 중단하지 않도록
                // 예외 처리 시 로깅 활성화 필요
                 log.warn("Failed to calc member similarity. target={}, other={}", memberId, otherId, e);
            }
        }
    }

    /**
     * (memberA, memberB)를 항상 (id 작은 쪽, 큰 쪽)으로 정규화해서 저장
     */
    private void upsertSimilarity(Member m1,
                                  Member m2,
                                  SimilarityScoreDetail score,
                                  LocalDateTime now) {

        Member memberA = (m1.getId() < m2.getId()) ? m1 : m2;
        Member memberB = (m1.getId() < m2.getId()) ? m2 : m1;

        Long aId = memberA.getId();
        Long bId = memberB.getId();

        SimilarityCache cache = similarityCacheRepository
                .findByMemberA_IdAndMemberB_Id(aId, bId)
                .orElseGet(() -> SimilarityCache.create(
                        memberA,
                        memberB,
                        score.getTotalScore(),
                        score.getGenreScore(),
                        score.getAudioScore(),
                        score.getArtistScore(),
                        score.getRarityScore(),
                        now
                ));

        if (cache.getId() != null) {
            cache.updateScores(
                    score.getTotalScore(),
                    score.getGenreScore(),
                    score.getAudioScore(),
                    score.getArtistScore(),
                    score.getRarityScore(),
                    now
            );
        }

        similarityCacheRepository.save(cache);
    }

    // ======================== 유사도 계산 ========================== //

    private SimilarityScoreDetail calculateMemberSimilarityScore(
            MemberMusicProfile a,
            MemberMusicProfile b
    ) {
        double[] v1 = buildAudioVector(a);
        double[] v2 = buildAudioVector(b);

        double audioScore = cosineSimilarity(v1, v2);

        double rarityScore = calculateRarityScore(
                a.getAvgRarity(),
                b.getAvgRarity(),
                a.getHiddenGemRatio(),
                b.getHiddenGemRatio()
        );

        // TODO: 장르/아티스트 유사도 추가하면 이쪽에서 genreScore/artistScore 포함
        double total = audioScore * 0.7 + rarityScore * 0.3;

        return new SimilarityScoreDetail(
                total,
                0.0,          // genreScore
                audioScore,
                0.0,          // artistScore
                rarityScore
        );
    }

    private double[] buildAudioVector(MemberMusicProfile p) {
        return new double[]{
                nullSafe(p.getAvgDanceability()),
                nullSafe(p.getAvgEnergy()),
                nullSafe(p.getAvgValence()),
                nullSafe(p.getAvgAcousticness()),
                nullSafe(p.getAvgInstrumentalness()),
                nullSafe(p.getAvgSpeechiness()),

                normalizeTempo(nullSafe(p.getAvgTempo())),      // 0-1 정규화
                normalizeLoudness(nullSafe(p.getAvgLoudness())), // 0-1 정규화
                normalizePopularity(nullSafe(p.getAvgPopularity())) // 0-1 정규화
        };
    }


    private double calculateRarityScore(
            Double rarity1, Double rarity2,
            Double hidden1, Double hidden2
    ) {
        double r1 = 1.0 - clamp(Math.abs(nullSafe(rarity1) - nullSafe(rarity2)), 0.0, 1.0);
        double r2 = 1.0 - clamp(Math.abs(nullSafe(hidden1) - nullSafe(hidden2)), 0.0, 1.0);
        return (r1 + r2) / 2.0;
    }

    private double cosineSimilarity(double[] v1, double[] v2) {
        double dot = 0, n1 = 0, n2 = 0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            n1 += v1[i] * v1[i];
            n2 += v2[i] * v2[i];
        }
        if (n1 == 0 || n2 == 0) return 0;
        return dot / (Math.sqrt(n1) * Math.sqrt(n2));
    }

    private double nullSafe(Double v) { return v == null ? 0.0 : v; }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // 정규화용 메서드
    private double normalizeTempo(double tempo) {
        // BPM → 0~1로 변환 (50~200 기준)
        return clamp((tempo - 50) / 150.0, 0.0, 1.0);
    }

    private double normalizeLoudness(double loudness) {
        // dB → 0~1로 변환 (-60~0 기준)
        return clamp((loudness + 60) / 60.0, 0.0, 1.0);
    }

    private double normalizePopularity(double popularity) {
        return clamp(popularity / 100.0, 0.0, 1.0);
    }

    // 로버스트 정규화도 테스트 해보고 추후에 고려
//    private double normalizeTempo(double tempo) {
//        return clamp((tempo - 40) / 190.0, 0.0, 1.0);
//    }
//
//    private double normalizeLoudness(double loudness) {
//        return clamp((loudness + 35) / 30.0, 0.0, 1.0);
//    }


    // =========================================================================
    // 2. 추천 멤버 리스트 조회 + 로그
    // =========================================================================

    /**
     * memberId 기준 추천 멤버 리스트 조회
     *  - similarity_cache에서 상위 N명
     *  - MemberRecommendItemDto로 변환
     *  - MemberRecommendation 로그 기록
     */
    @Transactional
    public List<MemberRecommendItemDto> getRecommendedMembers(Long memberId, int limit) {

        if (memberId == null) {
            return List.of();
        }

        // 1) 유사도 캐시에서 상위 100개 가져오기 (memberA or memberB)
        List<SimilarityCache> similarities =
                similarityCacheRepository.findTop100ByMemberA_IdOrMemberB_IdOrderByTotalScoreDesc(
                        memberId, memberId
                );

        if (similarities == null || similarities.isEmpty()) {
            return List.of();
        }

        // 2) 각 row에서 "상대 멤버 ID" + 점수 맵 만들기 (순서 유지)
        Map<Long, Double> scoreByOtherId = new LinkedHashMap<>();
        for (SimilarityCache sc : similarities) {
            Long aId = sc.getMemberA().getId();
            Long bId = sc.getMemberB().getId();
            Long otherId = aId.equals(memberId) ? bId : aId;
            if (otherId.equals(memberId)) continue;

            // 첫 번째로 등장한 점수만 사용 (이미 정렬되어 있음)
            scoreByOtherId.putIfAbsent(otherId, sc.getTotalScore());
        }

        List<Long> candidateIdsInOrder = new ArrayList<>(scoreByOtherId.keySet());
        if (candidateIdsInOrder.isEmpty()) {
            return List.of();
        }

        // limit 적용
        if (limit > 0 && candidateIdsInOrder.size() > limit) {
            candidateIdsInOrder = candidateIdsInOrder.subList(0, limit);
        }

        // 3) 멤버 엔티티 조회
        List<Member> members = memberRepository.findByIdIn(candidateIdsInOrder);
        Map<Long, Member> memberMap = members.stream()
                .collect(Collectors.toMap(Member::getId, m -> m));

        // 4) 내가 팔로우한 멤버 ID 세트 (null 방어)
        List<Long> followeeIdList = followRepository.findFolloweeIdsByFollowerId(memberId);
        Set<Long> followingIds = (followeeIdList != null)
                ? new HashSet<>(followeeIdList)
                : Collections.emptySet();

        // 5) 오늘의 음악 (groupId == null인 오늘의 개인 포스트) 조회
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        List<MusicPost> todayPosts = musicPostRepository.findTodayPostsByMemberIds(
                candidateIdsInOrder,
                startOfDay,
                endOfDay
        );

        // memberId -> 오늘의 개인 MusicPost (group == null) 매핑
        Map<Long, MusicPost> todayPersonalPostByMemberId = new HashMap<>();
        if (todayPosts != null) {
            for (MusicPost post : todayPosts) {
                if (post.getGroup() != null) continue; // 개인 오늘의 음악만

                Long ownerId = post.getMember().getId();
                todayPersonalPostByMemberId.putIfAbsent(ownerId, post);
            }
        }

        // 6) 순서를 유지하면서 DTO로 변환 (null 방어 포함)
        List<MemberRecommendItemDto> result = new ArrayList<>();

        for (Long otherId : candidateIdsInOrder) {
            Member other = memberMap.get(otherId);
            if (other == null) continue; // 삭제/비활성화 등

            MusicPost todayPost = todayPersonalPostByMemberId.get(otherId);

            String nickname = Optional.ofNullable(other.getNickname()).orElse("");
            String profileImageUrl = memberService.resolveProfileImageUrl(other);
            Boolean isPublic = (other.getIsPublic() != null)
                    ? other.getIsPublic()
                    : Boolean.TRUE;

            Boolean isFollowing = followingIds.contains(other.getId());

            String todayMusicName = null;
            String todayMusicArtistName = null;
            if (todayPost != null && todayPost.getMusic() != null) {
                todayMusicName = todayPost.getMusic().getName();
                todayMusicArtistName = todayPost.getMusic().getArtistName();
            }

            MemberRecommendItemDto dto = MemberRecommendItemDto.builder()
                    .memberId(other.getId())
                    .nickname(nickname)
                    .profileImageUrl(profileImageUrl)
                    .isPublic(isPublic)
                    .isFollowing(isFollowing)
                    .todayMusicName(todayMusicName)
                    .todayMusicArtistName(todayMusicArtistName)
                    .build();

            result.add(dto);
        }

        // 7) 추천 노출 로그 기록
        logMemberRecommendations(memberId, candidateIdsInOrder, scoreByOtherId, memberMap);

        return result;
    }

    @Transactional
    protected void logMemberRecommendations(Long memberId,
                                            List<Long> orderedOtherIds,
                                            Map<Long, Double> scoreByOtherId,
                                            Map<Long, Member> memberMap) {

        Member baseMember = memberRepository.findById(memberId).orElse(null);
        if (baseMember == null) return;

        LocalDateTime now = LocalDateTime.now();
        int rank = 1;

        for (Long otherId : orderedOtherIds) {
            Member recommended = memberMap.get(otherId);
            if (recommended == null) continue;

            Double score = scoreByOtherId.getOrDefault(otherId, 0.0);

            int finalRank = rank;
            MemberRecommendation recommendation =
                    memberRecommendationRepository
                            .findByMember_IdAndRecommendedMember_Id(memberId, otherId)
                            .orElseGet(() -> MemberRecommendation.create(
                                    baseMember,
                                    recommended,
                                    score,
                                    finalRank,
                                    now
                            ));

            if (recommendation.getId() != null) {
                recommendation.updateOnShown(score, rank, now);
            }

            memberRecommendationRepository.save(recommendation);
            rank++;
        }
    }

    // =========================================================================
    // 3. 클릭 / 팔로우 이벤트 로그
    // =========================================================================

    @Transactional
    public void markRecommendationClicked(Long memberId, Long recommendedMemberId) {
        memberRecommendationRepository
                .findByMember_IdAndRecommendedMember_Id(memberId, recommendedMemberId)
                .ifPresent(rec -> rec.markClicked(LocalDateTime.now()));
    }

    @Transactional
    public void markRecommendationFollowed(Long memberId, Long recommendedMemberId) {
        memberRecommendationRepository
                .findByMember_IdAndRecommendedMember_Id(memberId, recommendedMemberId)
                .ifPresent(rec -> rec.markFollowed(LocalDateTime.now()));
    }
}
