package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.level.GroupLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupProfileResponseDto {

    private Long groupId;
    private String name;
    private String description;
    private List<String> groupCategories;   // 그룹 카테고리 리스트
    private Integer totalMusicCount;        // 그룹 playlist의 음악 개수
    private Integer totalPostCount;         // 그룹 내 게시물 개수
    private Integer memberCount;
    private String imageUrl;                // 프로필 이미지 url
    private LocalDateTime createdAt;

    // 현재 사용자의 가입 여부
    private Boolean isJoined;

    // 그룹 레벨 관련 필드
    private Integer level;                  // 현재 그룹 레벨 (1~5)
    private Long exp;                       // 현재 누적 EXP
    private Long nextLevelExp;              // 다음 레벨이 요구하는 총 EXP (최대 레벨이면 null)
    private Long remainingExpToNext;        // 다음 레벨까지 남은 EXP (최대 레벨이면 0)

    /**
     * Group 엔티티와 외부 계산값을 받아 Dto 생성
     */
    public static GroupProfileResponseDto of(
            Group group,
            List<String> groupCategoryNames,
            int totalMusicCount,
            int totalPostCount,
            boolean isJoined
    ) {
        Long exp = group.getExp();              // Group 엔티티에 exp 필드 있다고 가정
        Integer level = group.getLevel();       // Group 엔티티에 level 필드 있다고 가정

        // 다음 레벨이 요구하는 "총 EXP" (ex. 레벨3 → 레벨4면 700)
        Long nextLevelExp = GroupLevel.getNextLevelRequiredExp(level);

        // 지금 EXP 기준으로 “얼마나 더 필요?” 계산
        Long remainingExpToNext = 0L;
        if (nextLevelExp != null) {
            long remain = nextLevelExp - exp;
            remainingExpToNext = Math.max(remain, 0L);
        }

        return GroupProfileResponseDto.builder()
                .groupId(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .groupCategories(groupCategoryNames)
                .totalMusicCount(totalMusicCount)
                .totalPostCount(totalPostCount)
                .memberCount(group.getMemberCount())
                .imageUrl(group.getGroupImage())
                .createdAt(group.getCreatedAt())
                // 레벨/EXP 정보 세팅
                .level(level)
                .exp(exp)
                .nextLevelExp(nextLevelExp)              // JSON에 null이면 "최대 레벨 상태" 의미
                .remainingExpToNext(remainingExpToNext)  // 프론트에서 게이지바 그릴 때 사용 (추후 디벨롭)
                .isJoined(isJoined)
                .build();
    }

    /**
     * 여러 Group 엔티티를 한 번에 DTO로 변환할 때 (선택사항)
     */
    public static List<GroupProfileResponseDto> fromEntities(
            List<Group> groups,
            Map<Long, List<String>> categoryNamesByGroup,
            Map<Long, Integer> musicCountByGroup,
            Map<Long, Integer> postCountByGroup,
            Map<Long, Boolean> isJoinedByGroup
    ) {
        return groups.stream()
                .map(group -> GroupProfileResponseDto.of(
                        group,
                        categoryNamesByGroup.getOrDefault(group.getId(), List.of()),
                        musicCountByGroup.getOrDefault(group.getId(), 0),
                        postCountByGroup.getOrDefault(group.getId(), 0),
                        isJoinedByGroup.getOrDefault(group.getId(), false)
                ))
                .toList();
    }
}