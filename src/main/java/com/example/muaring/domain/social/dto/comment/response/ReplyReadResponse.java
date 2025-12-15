package com.example.muaring.domain.social.dto.comment.response;

import com.example.muaring.domain.social.entity.Comment;
import lombok.Builder;

import java.time.format.DateTimeFormatter;

@Builder
public record ReplyReadResponse(
        Long commentId,
        String content,
        Long memberId,
        String memberNickname,
        String profileImgUrl,
        Boolean isDeleted,
        String createdAt
) {
    public static ReplyReadResponse from(Comment reply) {
        return ReplyReadResponse.builder()
                .commentId(reply.getId())
                .content(reply.getIsDeleted() ? "삭제된 댓글입니다." : reply.getContent())
                .memberId(reply.getIsDeleted() ? null : reply.getMember().getId())
                .memberNickname(reply.getIsDeleted() ? "알수없음" : reply.getMember().getNickname())
                .profileImgUrl(reply.getMember().getProfileImage().getUrl())
                .isDeleted(reply.getIsDeleted())
                .createdAt(reply.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }
}