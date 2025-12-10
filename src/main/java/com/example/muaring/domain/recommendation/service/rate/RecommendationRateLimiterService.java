package com.example.muaring.domain.recommendation.service.rate;

import com.example.muaring.domain.recommendation.exception.RecommendationException;
import com.example.muaring.domain.recommendation.response.RecommendationErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RecommendationRateLimiterService {

    private static final String KEY_PREFIX = "ratelimit:recommendations:member:";

    // 10초에 3번
    private static final long WINDOW_SECONDS = 10;
    private static final long LIMIT = 3;

    private final StringRedisTemplate redisTemplate;

    // INCR + EXPIRE를 원자적으로 처리하는 Lua 스크립트
    private static final String RATE_LIMIT_SCRIPT = """
        local count = redis.call('INCR', KEYS[1])
        if count == 1 then
            redis.call('EXPIRE', KEYS[1], ARGV[1])
        end
        return count
        """;

    // field 초기화 (final 아님, RequiredArgsConstructor 대상도 아님)
    private final RedisScript<Long> rateLimitScript =
            new DefaultRedisScript<>(RATE_LIMIT_SCRIPT, Long.class);

    // memberId 기준 RateLimit 체크
    public void checkMemberGroupRecommendationLimit(Long memberId) {

        String key = KEY_PREFIX + memberId;

        // 1) Lua 스크립트 실행 (INCR + EXPIRE 원자적 수행)
        Long count = redisTemplate.execute(
                rateLimitScript,
                Collections.singletonList(key),
                String.valueOf(WINDOW_SECONDS) // ARGV[1]
        );

        // 2) 제한 횟수 초과 → 예외 발생
        if (count != null && count > LIMIT) {
            throw new RecommendationException(RecommendationErrorCode.RATE_LIMIT_EXCEEDED);
        }
    }
}