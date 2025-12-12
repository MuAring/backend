package com.example.muaring.domain.group.level;

public enum GroupActivityType {

    TODAY_MUSIC_POST(10),   // 오늘의 음악 포스트 작성
    COMMENT(3),             // 댓글 작성
    LIKE(1),                // 좋아요
    MEMBER_JOINED(15);      // 새 멤버 가입

    private final int exp;

    GroupActivityType(int exp) {
        this.exp = exp;
    }

    public int getExp() {
        return exp;
    }
}