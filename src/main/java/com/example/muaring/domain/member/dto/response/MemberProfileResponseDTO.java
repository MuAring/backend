package com.example.muaring.domain.member.dto.response;

import com.example.muaring.domain.file.dto.ImageResponseDTO;
import com.example.muaring.domain.member.entity.Member;

public record MemberProfileResponseDTO(
        Long memberId,
        String nickname,
        ImageResponseDTO image
) {
    public static MemberProfileResponseDTO of(Member member, String bucketUrl) {
        return new  MemberProfileResponseDTO(
                member.getId(),
                member.getNickname(),
                ImageResponseDTO.of(member.getProfileImage(), bucketUrl)
        );
    }
}