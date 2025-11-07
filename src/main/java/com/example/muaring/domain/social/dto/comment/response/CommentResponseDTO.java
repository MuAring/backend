package com.example.muaring.domain.social.dto.comment.response;

import com.example.muaring.domain.social.entity.Comment;

public record CommentResponseDTO(
        Long commentId,
        Long postId,
        Long memberId,
        Long parentCommentId,
        String content
) {
    public static CommentResponseDTO of(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getPost().getId(),
                comment.getMember().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getContent()
        );
    }
}

