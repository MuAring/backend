package com.example.muaring.domain.social.dto.comment.response;

import com.example.muaring.domain.social.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReplyReadResponseDTO(
        Long commentId,
        String content,
        Long memberId,
        String memberNickname,
        Boolean isDeleted,
        LocalDateTime createdAt
) {
    public static ReplyReadResponseDTO from(Comment reply) {
        return ReplyReadResponseDTO.builder()
                .commentId(reply.getId())
                .content(reply.getIsDeleted() ? "삭제된 댓글입니다." : reply.getContent())
                .memberId(reply.getIsDeleted() ? null : reply.getMember().getId())
                .memberNickname(reply.getIsDeleted() ? "알수없음" : reply.getMember().getNickname())
                .isDeleted(reply.getIsDeleted())
                .createdAt(reply.getCreatedAt())
                .build();
    }
}