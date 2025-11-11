package com.example.muaring.domain.social.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.social.dto.comment.request.CommentCreateRequestDTO;
import com.example.muaring.domain.social.dto.comment.response.CommentResponseDTO;
import com.example.muaring.domain.social.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API", description = "댓글 관련 API입니다.")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    @Operation(summary = "댓글 등록", description = "댓글 등록 로직입니다.")
    public ResponseEntity<ApiResponse<CommentResponseDTO>> createComment(
            @PathVariable("postId") Long postId,
            @RequestBody CommentCreateRequestDTO requestDTO
    ) {
        CommentResponseDTO responseDTO = commentService.createComment(postId, requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(responseDTO, "댓글이 등록되었습니다."));
    }

    @DeleteMapping("comments/{commentId}")
    @Operation(summary = "댓글/답글 삭제", description = "댓글/답글 삭제 로직입니다.")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.ok("댓글이 삭제되었습니다."));
    }
}