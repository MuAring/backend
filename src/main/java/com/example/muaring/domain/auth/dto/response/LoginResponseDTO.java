package com.example.muaring.domain.auth.dto.response;

import com.example.muaring.domain.member.entity.Member;

// ✨소셜에서 받은 정보를 이용해 우리 DB에서 사용자를 찾고 우리 서비스용 JWT를 발급한 뒤 클라이언트에 응답하는 DTO (우리 서버 -> 클라이언트(안드로이드 앱))
public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        Long memberId,
        String email,
        String nickname,
        boolean hasNickname
) {
    public static LoginResponseDTO of(Member member, String accessToken, String refreshToken, String nickname, Boolean hasNickname) {
        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                hasNickname
        );
    }
}