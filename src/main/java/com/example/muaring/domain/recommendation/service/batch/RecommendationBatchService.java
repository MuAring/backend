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
    /**
     * [coderabbit]
     * 장시간 실행되는 배치 메서드에 @Transactional 사용은 위험합니다.
     * 전체 배치 작업을 하나의 트랜잭션으로 감싸면:
     *
     * DB 커넥션이 장시간 점유되어 커넥션 풀 고갈 가능
     * 누적된 엔티티로 인한 메모리 소비 증가
     * 마지막 멤버 처리 중 실패 시 전체 롤백
     */
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
