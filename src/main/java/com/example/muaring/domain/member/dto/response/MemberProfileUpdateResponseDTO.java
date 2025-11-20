package com.example.muaring.domain.member.dto.response;

import com.example.muaring.domain.file.dto.response.ImageResponseDTO;
import com.example.muaring.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record MemberProfileUpdateResponseDTO(
        Long memberId,
        String nickname,
        ImageResponseDTO image,
        Boolean isPublic,
        Boolean isDiscoveryEnabled
) {
    public static MemberProfileUpdateResponseDTO from(Member member, String bucketUrl) {
        return MemberProfileUpdateResponseDTO.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .image(ImageResponseDTO.of(member.getProfileImage(), bucketUrl))
                .isPublic(member.getIsPublic())
                .isDiscoveryEnabled(member.getDiscoveryEnabled())
                .build();
    }
}