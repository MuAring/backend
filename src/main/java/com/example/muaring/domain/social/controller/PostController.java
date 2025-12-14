package com.example.muaring.domain.social.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.group.dto.PostDetailReadResponse;
import com.example.muaring.domain.social.dto.post.*;
import com.example.muaring.domain.social.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<MusicPostDTO>> createMusicPost(
            @RequestBody MusicPostRequestDTO request
    ) {
        MusicPostDTO postDTO = postService.createMusicPost(request);
        return ResponseEntity.ok(ApiResponse.ok(postDTO, "음악 게시글이 작성되었습니다."));
    }

    // [GET] /post/followee/today
    // 팔로우한 사용자 + 내 게시물 조회
    @GetMapping("/post/followee/today")
    public ResponseEntity<ApiResponse<Page<MusicPostFeedResponseDto>>> getTodayFolloweePosts(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<MusicPostFeedResponseDto> response =
                postService.getTodayFolloweePosts(pageable);

        return ResponseEntity.ok(
                ApiResponse.ok(response, "팔로우한 사용자의 오늘 게시물을 조회했습니다.")
        );
    }

    @GetMapping("/post/today")
    public ResponseEntity<ApiResponse<TodayPostResponseDTO>> getTodayPost() {
        TodayPostResponseDTO response = postService.getTodayPostByMember();
        return ResponseEntity.ok(ApiResponse.ok(response, "오늘의 게시물 조회 완료"));
    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시물 상세 조회", description = "게시물의 기본 정보를 조회합니다. 댓글은 별도 API로 조회하세요.")
    public ResponseEntity<ApiResponse<PostDetailReadResponse>> getPostDetail(
            @PathVariable Long postId) {
        Long memberId = SecurityUtil.getMemberId();
        PostDetailReadResponse response = postService.getPostDetail(postId, memberId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(response, "게시물을 조회했습니다."));
    }
}