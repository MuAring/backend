package com.example.muaring.domain.member.dto.response;

public record NicknameCheckResponseDTO(
        String nickname,
        boolean isDuplicated
) {
    public static NicknameCheckResponseDTO of(String nickname, boolean isDuplicated) {
        return new NicknameCheckResponseDTO(nickname, isDuplicated);
    }
}