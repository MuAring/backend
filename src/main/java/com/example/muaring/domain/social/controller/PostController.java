package com.example.muaring.domain.social.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.social.dto.post.*;
import com.example.muaring.domain.social.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<MusicPostDTO>> createMusicPost(
            @RequestBody MusicPostRequestDTO request
    ) {
        MusicPostDTO postDTO = postService.createMusicPost(request);
        return ResponseEntity.ok(ApiResponse.ok(postDTO, "음악 게시글이 작성되었습니다."));
    }

    // [GET] /post/followee/today
    // 팔로우한 사용자 + 내 게시물 조회
    @GetMapping("/followee/today")
    public ResponseEntity<ApiResponse<Page<MusicPostFeedResponseDto>>> getTodayFolloweePosts(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<MusicPostFeedResponseDto> response =
                postService.getTodayFolloweePosts(pageable);

        return ResponseEntity.ok(
                ApiResponse.ok(response, "팔로우한 사용자의 오늘 게시물을 조회했습니다.")
        );
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<TodayPostResponseDTO>> getTodayPost() {
        TodayPostResponseDTO response = postService.getTodayPostByMember();
        return ResponseEntity.ok(ApiResponse.ok(response, "오늘의 게시물 조회 완료"));
    }
}