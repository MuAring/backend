package com.example.muaring.domain.social.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.social.dto.post.MusicPostDTO;
import com.example.muaring.domain.social.dto.post.MusicPostListResponseDTO;
import com.example.muaring.domain.social.dto.post.MusicPostRequestDTO;
import com.example.muaring.domain.social.dto.post.TodayPostResponse;
import com.example.muaring.domain.social.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<Page<MusicHistoryDTO>>> getMusicHistoryByMember(
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        @PageableDefault(page = 0, size = 10) Pageable pageable
    ) {
            Page<MusicHistoryDTO> history = postService.getMusicHistoryByMember(year, month, pageable);
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(history, "회원의 음악 히스토리 조회가 완료되었습니다."));
    }

    @GetMapping("/followee/today")
    public ResponseEntity<ApiResponse<List<MusicPostListResponseDTO>>> getTodayFolloweePosts() {
        List<MusicPostListResponseDTO> response = postService.getTodayFolloweePosts();
        return ResponseEntity.ok(ApiResponse.ok(response, "팔로우한 사용자의 게시물을 조회했습니다."));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<TodayPostResponse>> getTodayPost() {
        TodayPostResponse response = postService.getTodayPostByMember();
        return ResponseEntity.ok(ApiResponse.ok(response, "오늘의 게시물 조회 완료"));
    }

}
