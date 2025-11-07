package com.example.muaring.domain.social.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.social.dto.MusicPostDTO;
import com.example.muaring.domain.social.dto.MusicPostRequestDTO;
import com.example.muaring.domain.social.entity.MusicPost;
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
}
