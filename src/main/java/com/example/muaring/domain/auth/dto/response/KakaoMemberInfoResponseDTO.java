package com.example.muaring.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

// ✨ 카카오 사용자 정보 응답 DTO (카카오 서버 -> 우리 서버)
public record KakaoMemberInfoResponseDTO(

        @JsonProperty("id")
        Long kakaoId, // 카카오가 발급한 고유 ID

        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            @JsonProperty("email")
            String kakaoEmail
    ) {}
}