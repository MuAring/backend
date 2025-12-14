package com.example.muaring.domain.social.dto.comment.response;

import com.example.muaring.domain.social.entity.Comment;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.ALWAYS)  // null 값도 보여주도록
public record CommentResponse(
        Long commentId,
        Long postId,
        Long memberId,
        Long parentCommentId,
        String content
) {
    public static CommentResponse of(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getPost().getId(),
                comment.getMember().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                comment.getContent()
        );
    }
}

