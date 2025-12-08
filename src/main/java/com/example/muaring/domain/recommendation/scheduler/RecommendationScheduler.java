package com.example.muaring.domain.recommendation.scheduler;

import com.example.muaring.domain.recommendation.service.batch.RecommendationBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test") // 테스트 프로필에서는 스케줄러 안 돌리려면
public class RecommendationScheduler {

    private final RecommendationBatchService recommendationBatchService;

    /**
     * 매일 새벽 4시에 실행 (Asia/Seoul 기준)
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "${scheduler.recommendation.cron}", zone = "Asia/Seoul")
    public void runDailyRecommendationBatch() {
        log.info("[RecommendationScheduler] daily recommendation batch started");
        recommendationBatchService.recalcAllRecommendations();
        log.info("[RecommendationScheduler] daily recommendation batch finished");
    }
}
