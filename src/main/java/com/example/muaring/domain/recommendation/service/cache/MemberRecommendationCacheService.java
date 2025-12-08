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

    private final MemberRecommendationService memberRecommendationService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public List<MemberRecommendItemDto> getRecommendedMembersWithCache(Long memberId, int limit) {

        String key = buildKey(memberId);

        // 1) 캐시 조회
        String cachedJson = redisTemplate.opsForValue().get(key);
        if (cachedJson != null) {
            try {
                MemberRecommendItemDto[] arr =
                        objectMapper.readValue(cachedJson, MemberRecommendItemDto[].class);
                return Arrays.asList(arr);
            } catch (Exception ignored) {}
        }

        // 2) 캐시 미스 → 실제 서비스
        List<MemberRecommendItemDto> list =
                memberRecommendationService.getRecommendedMembers(memberId, limit);

        // 3) 캐시에 저장
        try {
            String json = objectMapper.writeValueAsString(list);
            redisTemplate.opsForValue().set(key, json, TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception ignored) {}

        return list;
    }

    public List<MemberRecommendItemDto> refreshRecommendedMembers(Long memberId, int limit) {
        redisTemplate.delete(buildKey(memberId));
        return getRecommendedMembersWithCache(memberId, limit);
    }

    private String buildKey(Long memberId) {
        return MEMBER_RECOMMEND_KEY_PREFIX + memberId + MEMBER_RECOMMEND_KEY_SUFFIX;
    }
}
