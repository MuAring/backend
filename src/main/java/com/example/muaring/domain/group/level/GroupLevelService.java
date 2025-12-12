package com.example.muaring.domain.group.level;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.group.response.GroupException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GroupLevelService {

    private static final int MAX_DAILY_EXP_PER_MEMBER = 30; // 각 멤버당 하루 최대 30

    private final GroupRepository groupRepository;
    private final RedisTemplate<String, Long> redisTemplate;

    /**
     * 그룹 활동 발생 시 호출
     * @param groupId  그룹 ID
     * @param memberId 활동한 멤버 ID (일일 캡 계산용)
     * @param activityType 활동 타입
     */
    @Transactional
    public void addActivity(Long groupId, Long memberId, GroupActivityType activityType) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        long expToAdd = calculateExpWithDailyCap(groupId, memberId, activityType);
        if (expToAdd <= 0) {
            return; // 이미 오늘 한도 채움
        }

        // 1. 그룹 EXP 증가
        group.addExp(expToAdd);

        // 2. 레벨 재계산
        GroupLevel newLevel = GroupLevel.calculateLevel(group.getExp(), group.getMemberCount());
        int beforeLevel = group.getLevel();

        if (newLevel.getLevel() > beforeLevel) {
            group.updateLevel(newLevel.getLevel());
            // TODO: 나중에 레벨업 알림/이벤트 발행
        }
    }

    /**
     * Redis를 이용해 해당 그룹에서 해당 멤버가 오늘 기여한 EXP를 추적
     * 하루 최대 30까지로 제한
     */
    private long calculateExpWithDailyCap(Long groupId, Long memberId, GroupActivityType activityType) {
        String today = LocalDate.now().toString(); // e.g. 2025-12-06
        String key = String.format("group:%d:member:%d:exp:%s", groupId, memberId, today);

        Long current = redisTemplate.opsForValue().get(key);
        if (current == null) {
            current = 0L;
        }

        if (current >= MAX_DAILY_EXP_PER_MEMBER) {
            return 0L;
        }

        long baseExp = activityType.getExp();
        long available = MAX_DAILY_EXP_PER_MEMBER - current;
        long finalExp = Math.min(baseExp, available);

        // Redis에 저장 (TTL은 대략 하루)
        redisTemplate.opsForValue().set(
                key,
                current + finalExp,
                Duration.ofHours(24)
        );

        return finalExp;
    }
}