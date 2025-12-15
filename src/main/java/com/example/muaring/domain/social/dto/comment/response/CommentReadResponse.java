package com.example.muaring.domain.social.dto.comment.response;

import com.example.muaring.domain.social.entity.Comment;
import lombok.Builder;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Builder
public record CommentReadResponse(
        Long commentId,
        String content,
        Long memberId,
        String memberNickname,
        String profileImgUrl,
        Boolean isDeleted,
        String createdAt,
        List<ReplyReadResponse> replies
) {
    public static CommentReadResponse from(Comment comment) {
        return CommentReadResponse.builder()
                .commentId(comment.getId())
                .content(comment.getIsDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .memberId(comment.getIsDeleted() ? null : comment.getMember().getId())
                .memberNickname(comment.getIsDeleted() ? "알수없음" : comment.getMember().getNickname())
                .profileImgUrl(comment.getMember().getProfileImage().getUrl())
                .isDeleted(comment.getIsDeleted())
                .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .replies(comment.getReplies().stream()
                        .map(ReplyReadResponse::from)
                        .toList())
                .build();
    }
}
