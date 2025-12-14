package com.example.muaring.domain.social.dto.comment.request;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        String content
) { }