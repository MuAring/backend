package com.example.muaring.domain.recommendation.service.cache;

import com.example.muaring.domain.recommendation.dto.MemberRecommendItemDto;
import com.example.muaring.domain.recommendation.service.MemberRecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MemberRecommendationCacheService {

    private static final String MEMBER_RECOMMEND_KEY_PREFIX = "recommendations:member:";
    private static final String MEMBER_RECOMMEND_KEY_SUFFIX = ":members";

    // 6시간 TTL
    private static final long TTL_SECONDS = 6 * 60 * 60;

    // 추천 최대 개수
    private static final int MAX_LIMIT = 50;

    // 기본 limit
    private static final int DEFAULT_LIMIT = 20;

    private final MemberRecommendationService memberRecommendationService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public List<MemberRecommendItemDto> getRecommendedMembersWithCache(Long memberId, int limit) {

        int effectiveLimit = normalizeLimit(limit);
        String key = buildKey(memberId);

        // 1) 캐시 조회 (항상 full list)
        String cachedJson = redisTemplate.opsForValue().get(key);
        if (cachedJson != null) {
            try {
                MemberRecommendItemDto[] arr =
                        objectMapper.readValue(cachedJson, MemberRecommendItemDto[].class);

                List<MemberRecommendItemDto> fullList = Arrays.asList(arr);

                // 요청한 limit만큼 잘라서 반환
                if (fullList.size() > effectiveLimit) {
                    return fullList.subList(0, effectiveLimit);
                }
                return fullList;

            } catch (Exception ignored) {}
        }

        // 2) 캐시 미스 → 실제 추천 서비스 호출 (full list로 받기)
        List<MemberRecommendItemDto> fullList =
                memberRecommendationService.getRecommendedMembers(memberId, MAX_LIMIT);

        // 3) 캐시에 저장 (full list)
        try {
            String json = objectMapper.writeValueAsString(fullList);
            redisTemplate.opsForValue().set(key, json, TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception ignored) {}

        // 4) limit 적용 후 반환
        if (fullList.size() > effectiveLimit) {
            return fullList.subList(0, effectiveLimit);
        }

        return fullList;
    }

    public List<MemberRecommendItemDto> refreshRecommendedMembers(Long memberId, int limit) {
        redisTemplate.delete(buildKey(memberId));
        return getRecommendedMembersWithCache(memberId, limit);
    }

    private String buildKey(Long memberId) {
        return MEMBER_RECOMMEND_KEY_PREFIX + memberId + MEMBER_RECOMMEND_KEY_SUFFIX;
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}
