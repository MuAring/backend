package com.example.muaring.domain.recommendation.service.cache;

import com.example.muaring.domain.recommendation.dto.GroupRecommendItemDto;
import com.example.muaring.domain.recommendation.dto.GroupRecommendListResponseDto;
import com.example.muaring.domain.recommendation.service.GroupRecommendationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GroupRecommendationCacheService {

    private static final String GROUP_RECOMMEND_KEY_PREFIX = "recommendations:group:member:";
    private static final long TTL_SECONDS = 6 * 60 * 60;
    private static final int MAX_LIMIT = 50;
    private static final int DEFAULT_LIMIT = 20;

    private final GroupRecommendationService groupRecommendationService;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 그룹 추천 조회 (Redis 캐시 포함)
     * - 캐시에는 항상 MAX_LIMIT까지의 full 리스트 저장
     * - 요청마다 limit에 맞게 잘라서 반환
     */
    public GroupRecommendListResponseDto getRecommendedGroupsWithCache(Long memberId, int limit) {

        int effectiveLimit = normalizeLimit(limit);
        String key = buildKey(memberId);

        // 1) Redis 캐시 조회
        String cachedJson = redisTemplate.opsForValue().get(key);
        if (cachedJson != null) {
            try {
                GroupRecommendListResponseDto cached =
                        objectMapper.readValue(cachedJson, GroupRecommendListResponseDto.class);

                List<GroupRecommendItemDto> fullList = cached.getGroups();

                List<GroupRecommendItemDto> sliced =
                        (fullList.size() > effectiveLimit)
                                ? fullList.subList(0, effectiveLimit)
                                : fullList;

                // memberId는 DTO 안에도 있지만, 어차피 호출하는 쪽에서 넘겨준 값 그대로 쓰는 게 명확해서 이렇게 씀
                return GroupRecommendListResponseDto.of(memberId, sliced);

            } catch (Exception ignored) {
            }
        }

        // 2) 캐시 미스 → 실제 추천 서비스 호출 (MAX_LIMIT까지 full list)
        GroupRecommendListResponseDto fullResponse =
                groupRecommendationService.getRecommendedGroupsForMember(memberId, MAX_LIMIT);

        List<GroupRecommendItemDto> fullList = fullResponse.getGroups();

        // 3) full response 캐시에 저장
        try {
            String json = objectMapper.writeValueAsString(fullResponse);
            redisTemplate.opsForValue().set(key, json, TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception ignored) {
            // 직렬화 실패해도 API는 정상 동작하게 그냥 무시
        }

        // 4) limit 적용 후 반환
        List<GroupRecommendItemDto> sliced =
                (fullList.size() > effectiveLimit)
                        ? fullList.subList(0, effectiveLimit)
                        : fullList;

        return GroupRecommendListResponseDto.of(memberId, sliced);
    }

    // 강제 갱신용 (관리자/설정 페이지용 API에서 호출)
    // 캐시 삭제 후 다시 계산
    public GroupRecommendListResponseDto refreshRecommendedGroups(Long memberId, int limit) {
        redisTemplate.delete(buildKey(memberId));
        return getRecommendedGroupsWithCache(memberId, limit);
    }

    private String buildKey(Long memberId) {
        return GROUP_RECOMMEND_KEY_PREFIX + memberId;
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }
}