package com.example.muaring.domain.social.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.social.dto.like.response.LikeResponseDTO;
import com.example.muaring.domain.social.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts/{postId}/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    @Operation(summary = "좋아요 등록/취소", description = "좋아요 등록/취소 로직입니다.(토글)")
    public ResponseEntity<ApiResponse<LikeResponseDTO>> handleLike(
            @PathVariable("postId") Long postId
    ) {
        Long memberId = SecurityUtil.getMemberId();
        LikeResponseDTO responseDTO = likeService.handleLike(memberId, postId);
        return ResponseEntity.ok(
                ApiResponse.ok(responseDTO, "좋아요 상태가 변경되었습니다.")
        );
    }
}