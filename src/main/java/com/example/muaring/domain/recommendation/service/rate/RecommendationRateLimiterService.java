package com.example.muaring.domain.recommendation.service.rate;

import com.example.muaring.domain.recommendation.exception.RecommendationException;
import com.example.muaring.domain.recommendation.response.RecommendationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RecommendationRateLimiterService {

    private static final String KEY_PREFIX = "ratelimit:recommendations:member:";

    // 10초에 3번
    private static final long WINDOW_SECONDS = 10;
    private static final long LIMIT = 3;

    private final StringRedisTemplate redisTemplate;

    // memberId 기준 RateLimit 체크
    public void checkMemberGroupRecommendationLimit(Long memberId) {

        String key = KEY_PREFIX + memberId;

        // 1) 증가시키고 count 값 받기
        Long count = redisTemplate.opsForValue().increment(key);

        // 2) TTL 설정 (첫 호출일 때만)
        if (count != null && count == 1L) {
            redisTemplate.expire(key, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        // 3) 제한 횟수 초과 → 예외 발생
        if (count != null && count > LIMIT) {
            throw new RecommendationException(RecommendationErrorCode.RATE_LIMIT_EXCEEDED);
        }
    }
}