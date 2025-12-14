package com.example.muaring.domain.recommendation.controller;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.recommendation.dto.GroupRecommendListResponseDto;
import com.example.muaring.domain.recommendation.dto.MemberRecommendItemDto;
import com.example.muaring.domain.recommendation.service.GroupRecommendationService;
import com.example.muaring.domain.recommendation.service.MemberRecommendationService;
import com.example.muaring.domain.recommendation.service.cache.GroupRecommendationCacheService;
import com.example.muaring.domain.recommendation.service.cache.MemberRecommendationCacheService;
import com.example.muaring.domain.recommendation.service.rate.RecommendationRateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final GroupRecommendationCacheService groupRecommendationCacheService;
    private final MemberRecommendationCacheService memberRecommendationCacheService;
    private final RecommendationRateLimiterService rateLimiterService;
    private final GroupRecommendationService groupRecommendationService;
    private final MemberRecommendationService memberRecommendationService;

    // ===================== 그룹 추천 ===================== //

    @GetMapping("/groups-members")
    public ResponseEntity<GroupRecommendListResponseDto> getRecommendedGroups(
            @RequestParam(defaultValue = "20") int limit
    ) {
        Long memberId = SecurityUtil.getMemberId();
        // Rate Limit 체크
        rateLimiterService.checkMemberGroupRecommendationLimit(memberId);

        GroupRecommendListResponseDto response =
                groupRecommendationCacheService.getRecommendedGroupsWithCache(memberId, limit);
        return ResponseEntity.ok(response);
    }

    // 강제 갱신 API
    @PutMapping("/groups-members/{memberId}")
    public ResponseEntity<GroupRecommendListResponseDto> refreshRecommendedGroups(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        rateLimiterService.checkMemberGroupRecommendationLimit(memberId);

        GroupRecommendListResponseDto response =
                groupRecommendationCacheService.refreshRecommendedGroups(memberId, limit);
        return ResponseEntity.ok(response);
    }

    // 추천 카드 클릭 이벤트 로그
    @PostMapping("/groups-members/{groupId}/click")
    public ResponseEntity<Void> markGroupRecommendationClicked(
            @PathVariable Long groupId
    ) {
        Long memberId = SecurityUtil.getMemberId();
        groupRecommendationService.markRecommendationClicked(memberId, groupId);
        return ResponseEntity.ok().build();
    }

    // 추천에서 그룹 가입 성공 이벤트 로그
    @PostMapping("/groups-members/{groupId}/join")
    public ResponseEntity<Void> markGroupRecommendationJoined(
            @PathVariable Long groupId
    ) {
        Long memberId = SecurityUtil.getMemberId();
        groupRecommendationService.markRecommendationJoined(memberId, groupId);
        return ResponseEntity.ok().build();
    }

    // ===================== 멤버 추천 ===================== //

    @GetMapping("/members")
    public ResponseEntity<List<MemberRecommendItemDto>> getRecommendedMembers(
            @RequestParam(defaultValue = "20") int limit
    ) {
        Long memberId = SecurityUtil.getMemberId();
        // 멤버 추천도 같은 RateLimit 버킷 사용
        rateLimiterService.checkMemberGroupRecommendationLimit(memberId);

        List<MemberRecommendItemDto> response =
                memberRecommendationCacheService.getRecommendedMembersWithCache(memberId, limit);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<List<MemberRecommendItemDto>> refreshRecommendedMembers(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "20") int limit
    ) {
        rateLimiterService.checkMemberGroupRecommendationLimit(memberId);

        List<MemberRecommendItemDto> response =
                memberRecommendationCacheService.refreshRecommendedMembers(memberId, limit);
        return ResponseEntity.ok(response);
    }

    // 추천 멤버 카드 클릭 이벤트 로그
    @PostMapping("/members/{targetMemberId}/click")
    public ResponseEntity<Void> markMemberRecommendationClicked(
            @PathVariable Long targetMemberId
    ) {
        Long memberId = SecurityUtil.getMemberId();
        memberRecommendationService.markRecommendationClicked(memberId, targetMemberId);
        return ResponseEntity.ok().build();
    }

    // 추천에서 멤버 팔로우 성공 이벤트 로그
    @PostMapping("/members/{targetMemberId}/follow")
    public ResponseEntity<Void> markMemberRecommendationFollowed(
            @PathVariable Long targetMemberId
    ) {
        Long memberId = SecurityUtil.getMemberId();
        memberRecommendationService.markRecommendationFollowed(memberId, targetMemberId);
        return ResponseEntity.ok().build();
    }
}