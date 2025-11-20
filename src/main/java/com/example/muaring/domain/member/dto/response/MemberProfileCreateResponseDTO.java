package com.example.muaring.domain.member.dto.response;

import com.example.muaring.domain.file.dto.response.ImageResponseDTO;
import com.example.muaring.domain.member.entity.Member;

public record MemberProfileCreateResponseDTO(
        Long memberId,
        String nickname,
        ImageResponseDTO image
) {
    public static MemberProfileCreateResponseDTO of(Member member, String bucketUrl) {
        return new MemberProfileCreateResponseDTO(
                member.getId(),
                member.getNickname(),
                ImageResponseDTO.of(member.getProfileImage(), bucketUrl)
        );
    }
}