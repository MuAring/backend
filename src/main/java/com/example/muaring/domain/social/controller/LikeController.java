package com.example.muaring.domain.social.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.social.dto.LikeResponseDTO;
import com.example.muaring.domain.social.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts/{postId}/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<ApiResponse<LikeResponseDTO>> handleLike(
            @PathVariable("postId") Long postId
    ) {
        LikeResponseDTO responseDTO = likeService.handleLike(postId);
        return ResponseEntity.ok(
                ApiResponse.ok(responseDTO, "좋아요 상태가 변경되었습니다.")
        );
    }
}