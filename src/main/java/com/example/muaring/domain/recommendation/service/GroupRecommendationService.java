package com.example.muaring.domain.recommendation.service;

import com.example.muaring.domain.common.ProfileStatus;
import com.example.muaring.domain.recommendation.dto.GroupRecommendItemDto;
import com.example.muaring.domain.recommendation.dto.GroupRecommendListResponseDto;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupMusicProfile;
import com.example.muaring.domain.group.repository.GroupCategoryMappingRepository;
import com.example.muaring.domain.group.repository.GroupMemberRepository;
import com.example.muaring.domain.group.repository.GroupMusicProfileRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.group.repository.projection.GroupIdCategoryNameProjection;
import com.example.muaring.domain.member.entity.MemberMusicProfile;
import com.example.muaring.domain.member.repository.MemberMusicProfileRepository;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.recommendation.entity.GroupRecommendation;
import com.example.muaring.domain.recommendation.entity.MemberGroupSimilarityCache;
import com.example.muaring.domain.recommendation.model.SimilarityScoreDetail;
import com.example.muaring.domain.recommendation.repository.GroupRecommendationRepository;
import com.example.muaring.domain.recommendation.repository.MemberGroupSimilarityCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupRecommendationService {

    private final MemberMusicProfileRepository memberMusicProfileRepository;
    private final GroupMusicProfileRepository groupMusicProfileRepository;
    private final MemberGroupSimilarityCacheRepository cacheRepository;

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupCategoryMappingRepository groupCategoryMappingRepository;

    private final MemberRepository memberRepository;
    private final GroupRecommendationRepository groupRecommendationRepository;


    // 사용자-그룹 추천용 계산
    @Transactional
    public void recalcMemberGroupSimilaritiesForMember(Long memberId) {

        MemberMusicProfile memberProfile = memberMusicProfileRepository.findById(memberId)
                .orElse(null);

        // 프로필 없거나 준비 안됐으면 그냥 캐시 삭제하고 종료
        if (memberProfile == null || memberProfile.getStatus() != ProfileStatus.READY) {
            cacheRepository.deleteAllByMemberId(memberId);
            return;
        }

        // 기존 캐시 삭제
        cacheRepository.deleteAllByMemberId(memberId);

        // READY 그룹 프로필만 조회
        List<GroupMusicProfile> groupProfiles =
                groupMusicProfileRepository.findByStatus(ProfileStatus.READY);

        LocalDateTime now = LocalDateTime.now();

        for (GroupMusicProfile groupProfile : groupProfiles) {
            Group group = groupProfile.getGroup();

            // 비공개 그룹 추천 제외
            if (!group.getIsPublic()) continue;

            SimilarityScoreDetail scoreDetail =
                    calculateMemberGroupScore(memberProfile, groupProfile);

            if (scoreDetail.getTotalScore() <= 0.0) continue;

            upsertCache(memberId, group.getId(), scoreDetail, now);
        }
    }

    private void upsertCache(Long memberId,
                             Long groupId,
                             SimilarityScoreDetail score,
                             LocalDateTime now) {

        MemberGroupSimilarityCache cache =
                cacheRepository.findByMemberIdAndGroupId(memberId, groupId)
                        .orElseGet(() -> MemberGroupSimilarityCache.of(
                                memberId,
                                groupId,
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

        cacheRepository.save(cache);
    }

    // ======================== 유사도 계산 ========================== //

    private SimilarityScoreDetail calculateMemberGroupScore(
            MemberMusicProfile member,
            GroupMusicProfile group
    ) {
        double[] mv = buildAudioVector(member);
        double[] gv = buildAudioVector(group);

        double audioScore = cosineSimilarity(mv, gv);

        double rarityScore = calculateRarityScore(
                member.getAvgRarity(),
                group.getAvgRarity(),
                member.getHiddenGemRatio(),
                group.getHiddenGemRatio()
        );

        // 총점 가중치 (기본 튜닝)
        double total = audioScore * 0.7 + rarityScore * 0.3;

        return new SimilarityScoreDetail(
                total,
                0.0,              // genreScore 나중에 추가
                audioScore,
                0.0,              // artistScore 나중에 추가
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
                nullSafe(p.getAvgTempo()),
                nullSafe(p.getAvgLoudness()),
                nullSafe(p.getAvgPopularity())
        };
    }

    private double[] buildAudioVector(GroupMusicProfile p) {
        return new double[]{
                nullSafe(p.getAvgDanceability()),
                nullSafe(p.getAvgEnergy()),
                nullSafe(p.getAvgValence()),
                nullSafe(p.getAvgAcousticness()),
                nullSafe(p.getAvgInstrumentalness()),
                nullSafe(p.getAvgSpeechiness()),
                nullSafe(p.getAvgTempo()),
                nullSafe(p.getAvgLoudness()),
                nullSafe(p.getAvgPopularity())
        };
    }

    private double calculateRarityScore(
            Double memberRarity, Double groupRarity,
            Double memberHidden, Double groupHidden
    ) {
        double r1 = 1.0 - clamp(Math.abs(nullSafe(memberRarity) - nullSafe(groupRarity)), 0.0, 1.0);
        double r2 = 1.0 - clamp(Math.abs(nullSafe(memberHidden) - nullSafe(groupHidden)), 0.0, 1.0);
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

    // ====================== 2. 추천 결과 조회 (DTO 반환) ====================== //

    /**
     * memberId 기준 그룹 추천 리스트 조회
     *  - member_group_similarity_cache 에서 Top N 가져옴
     *  - Group, 카테고리, isJoined 붙여서 DTO로 반환
     *  - 프론트에서는 isJoined == false 만 필터링해서 쓰기
     */
    public GroupRecommendListResponseDto getRecommendedGroupsForMember(Long memberId, int limit) {

        // 1) 유사도 캐시에서 상위 N개 가져오기
        List<MemberGroupSimilarityCache> caches =
                cacheRepository.findTop50ByMemberIdOrderByTotalScoreDesc(memberId);

        if (caches.isEmpty()) {
            return GroupRecommendListResponseDto.of(memberId, List.of());
        }

        // limit 적용 (안전하게)
        if (limit > 0 && caches.size() > limit) {
            caches = caches.subList(0, limit);
        }

        // 2) groupId 목록 추출
        List<Long> groupIdsInOrder = caches.stream()
                .map(MemberGroupSimilarityCache::getGroupId)
                .toList();

        // 중복 제거해서 실제 조회용 ID 목록 만들기
        List<Long> distinctGroupIds = groupIdsInOrder.stream()
                .distinct()
                .toList();

        // 3) 그룹 엔티티 조회
        List<Group> groups = groupRepository.findAllById(distinctGroupIds);
        Map<Long, Group> groupMap = groups.stream()
                .collect(Collectors.toMap(Group::getId, g -> g));

        // 4) 내가 가입한 그룹 ID 세트
        Set<Long> myJoinedGroupIds = groupMemberRepository.findGroupIdsByMemberId(memberId);

        // 5) 카테고리 이름 projection 조회 → map 변환
        List<GroupIdCategoryNameProjection> pairs =
                groupCategoryMappingRepository.findPairsWithNamesByGroupIds(distinctGroupIds);

        Map<Long, List<String>> categoryNamesByGroup = buildCategoryNamesMap(pairs);

        // 6) 랭킹 순서를 유지하면서 DTO 만들기
        List<GroupRecommendItemDto> items = new ArrayList<>();

        for (MemberGroupSimilarityCache cache : caches) {
            Long gid = cache.getGroupId();
            Group group = groupMap.get(gid);
            if (group == null) continue;
            if (Boolean.FALSE.equals(group.getIsPublic())) continue; // 안전하게 한 번 더 필터

            List<String> categoryNames =
                    categoryNamesByGroup.getOrDefault(gid, List.of());

            boolean isJoined = myJoinedGroupIds.contains(gid);

            items.add(
                    GroupRecommendItemDto.of(
                            gid,
                            group.getGroupImage(),
                            group.getName(),
                            categoryNames,
                            isJoined
                    )
            );
        }

        logGroupRecommendations(memberId, caches, groupMap);

        return GroupRecommendListResponseDto.of(memberId, items);
    }

    @Transactional
    protected void logGroupRecommendations(
            Long memberId,
            List<MemberGroupSimilarityCache> caches,
            Map<Long, Group> groupMap
    ) {
        var memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            return; // 이건 거의 안 나겠지만, 방어용
        }
        var member = memberOpt.get();
        LocalDateTime now = LocalDateTime.now();

        int rank = 1;
        for (MemberGroupSimilarityCache cache : caches) {
            Long groupId = cache.getGroupId();
            Group group = groupMap.get(groupId);
            if (group == null) continue;

            // 비공개 그룹은 기록도 안 함
            if (Boolean.FALSE.equals(group.getIsPublic())) continue;

            GroupRecommendation recommendation =
                    groupRecommendationRepository.findByMember_IdAndRecommendedGroup_Id(memberId, groupId)
                            .orElse(null);

            if (recommendation == null) {
                recommendation = GroupRecommendation.create(
                        member,
                        group,
                        cache.getTotalScore(),
                        rank,
                        now
                );
            } else {
                recommendation.updateOnShown(
                        cache.getTotalScore(),
                        rank,
                        now
                );
            }

            groupRecommendationRepository.save(recommendation);
            rank++;
        }
    }

    private Map<Long, List<String>> buildCategoryNamesMap(
            List<GroupIdCategoryNameProjection> pairs
    ) {
        return pairs.stream()
                .collect(Collectors.groupingBy(
                        GroupIdCategoryNameProjection::getGroupId,
                        Collectors.mapping(
                                GroupIdCategoryNameProjection::getCategoryCode, // projection 필드명에 맞게
                                Collectors.toList()
                        )
                ));
    }

    @Transactional
    public void markRecommendationClicked(Long memberId, Long groupId) {
        groupRecommendationRepository.findByMember_IdAndRecommendedGroup_Id(memberId, groupId)
                .ifPresent(rec -> rec.markClicked(LocalDateTime.now()));
    }

    @Transactional
    public void markRecommendationJoined(Long memberId, Long groupId) {
        groupRecommendationRepository.findByMember_IdAndRecommendedGroup_Id(memberId, groupId)
                .ifPresent(rec -> rec.markJoined(LocalDateTime.now()));
    }

}