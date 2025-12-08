package com.example.muaring.domain.recommendation.service.batch;

import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.recommendation.service.GroupRecommendationService;
import com.example.muaring.domain.recommendation.service.MemberRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationBatchService {

    private final MemberRepository memberRepository;
    private final MemberRecommendationService memberRecommendationService;
    private final GroupRecommendationService groupRecommendationService;
    // 필요하면 MemberMusicProfileService, GroupMusicProfileService도 주입해서 같이 호출

    /**
     * 전체 멤버에 대해 추천 관련 계산 배치 실행
     * - 멤버 음악 프로필 / 그룹 음악 프로필 계산이 이미 선행됐다고 가정
     * - 멤버↔멤버 유사도
     * - 멤버↔그룹 유사도
     */
    @Transactional
    public void recalcAllRecommendations() {

        int page = 0;
        int size = 500; // 한번에 처리할 멤버 수

        while (true) {
            Page<Member> memberPage = memberRepository.findAll(PageRequest.of(page, size));

            if (memberPage.isEmpty()) {
                break;
            }

            log.info("[RecommendationBatch] start page={}, size={}", page, size);

            memberPage.forEach(member -> {
                Long memberId = member.getId();
                try {
                    // 1) 멤버↔멤버 유사도 재계산
                    memberRecommendationService.recalcSimilaritiesForMember(memberId);

                    // 2) 멤버↔그룹 유사도 재계산
                    groupRecommendationService.recalcMemberGroupSimilaritiesForMember(memberId);
                } catch (Exception e) {
                    log.warn("[RecommendationBatch] failed for memberId={}", memberId, e);
                }
            });

            log.info("[RecommendationBatch] done page={}, size={}", page, size);

            if (!memberPage.hasNext()) {
                break;
            }
            page++;
        }

        log.info("[RecommendationBatch] all members processed");
    }
}
