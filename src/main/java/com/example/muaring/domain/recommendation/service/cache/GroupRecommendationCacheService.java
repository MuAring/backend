package com.example.muaring.domain.recommendation.service.cache;

import com.example.muaring.domain.recommendation.dto.GroupRecommendListResponseDto;
import com.example.muaring.domain.recommendation.service.GroupRecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GroupRecommendationCacheService {

    private static final String MEMBER_GROUP_RECOMMEND_KEY_PREFIX = "recommendations:member:";
    private static final String MEMBER_GROUP_RECOMMEND_KEY_SUFFIX = ":groups";

    // 6시간 TTL
    private static final long TTL_SECONDS = 6 * 60 * 60;

    private final GroupRecommendationService groupRecommendationService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    // 캐시를 고려한 추천 조회
    public GroupRecommendListResponseDto getRecommendedGroupsWithCache(Long memberId, int limit) {

        String key = buildKey(memberId);

        // 1) Redis 캐시 조회
        String cachedJson = redisTemplate.opsForValue().get(key);
        if (cachedJson != null) {
            try {
                return objectMapper.readValue(cachedJson, GroupRecommendListResponseDto.class);
            } catch (Exception e) {
                // JSON 깨져있으면 무시하고 DB에서 새로 가져옴
            }
        }

        // 2) 캐시 미스 → 실제 서비스 호출
        GroupRecommendListResponseDto response =
                groupRecommendationService.getRecommendedGroupsForMember(memberId, limit);

        // 3) 결과를 캐시에 저장
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(key, json, TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            // 직렬화 실패해도 API는 정상 동작하게 그냥 무시
        }

        return response;
    }

    // 강제 갱신용 (관리자/설정 페이지용 API에서 호출)
    public GroupRecommendListResponseDto refreshRecommendedGroups(Long memberId, int limit) {
        redisTemplate.delete(buildKey(memberId));
        // 필요하면 여기서 바로 recalcMemberGroupSimilaritiesForMember(memberId) 먼저 돌려도 됨
        return getRecommendedGroupsWithCache(memberId, limit);
    }

    private String buildKey(Long memberId) {
        return MEMBER_GROUP_RECOMMEND_KEY_PREFIX + memberId + MEMBER_GROUP_RECOMMEND_KEY_SUFFIX;
    }
}
