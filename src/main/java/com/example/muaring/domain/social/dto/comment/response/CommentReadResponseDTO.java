package com.example.muaring.domain.social.dto.comment.response;

import com.example.muaring.domain.social.entity.Comment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CommentReadResponseDTO(
        Long commentId,
        String content,
        Long memberId,
        String memberNickname,
        Boolean isDeleted,
        LocalDateTime createdAt,
        List<ReplyReadResponseDTO> replies
) {
    public static CommentReadResponseDTO from(Comment comment) {
        return CommentReadResponseDTO.builder()
                .commentId(comment.getId())
                .content(comment.getIsDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .memberId(comment.getIsDeleted() ? null : comment.getMember().getId())
                .memberNickname(comment.getIsDeleted() ? "알수없음" : comment.getMember().getNickname())
                .isDeleted(comment.getIsDeleted())
                .createdAt(comment.getCreatedAt())
                .replies(comment.getReplies().stream()
                        .map(ReplyReadResponseDTO::from)
                        .toList())
                .build();
    }
}
