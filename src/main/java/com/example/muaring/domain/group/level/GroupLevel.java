package com.example.muaring.domain.group.level;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupLevel {

    LEVEL_1(1,    0,  1),
    LEVEL_2(2,  100,  3),
    LEVEL_3(3,  300,  5),
    LEVEL_4(4,  700, 10),
    LEVEL_5(5, 1500, 20);

    private final int level;
    private final long requiredExp;
    private final int requiredMemberCount;

    public static GroupLevel calculateLevel(long exp, int memberCount) {
        GroupLevel result = LEVEL_1;
        for (GroupLevel level : values()) {
            if (exp >= level.requiredExp && memberCount >= level.requiredMemberCount) {
                result = level;
            }
        }
        return result;
    }

    // 현재 레벨에서 다음 레벨이 요구하는 EXP (없으면 null)
    public static Long getNextLevelRequiredExp(int currentLevel) {
        GroupLevel[] levels = values();
        for (int i = 0; i < levels.length; i++) {
            if (levels[i].level == currentLevel) {
                if (i + 1 < levels.length) {
                    return levels[i + 1].requiredExp;
                } else {
                    return null; // 최대 레벨
                }
            }
        }
        return null;
    }
}