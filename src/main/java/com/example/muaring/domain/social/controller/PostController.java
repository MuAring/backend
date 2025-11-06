package com.example.muaring.domain.social.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.social.dto.MusicPostRequestDTO;
import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.service.PostService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<ApiResponse<MusicPost>> createMusicPost(@RequestBody MusicPostRequestDTO request) {
        MusicPost post = postService.createMusicPost(
                request.getMemberId(),
                request.getGroupId(),
                request.getSpotifyId(),
                request.getContent()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(post, "음악 게시글이 작성되었습니다."));
    }

    @GetMapping("/history/{memberId}")
    public ResponseEntity<ApiResponse<List<MusicHistoryDTO>>> getMusicHistoryByMember(@PathVariable Long memberId) {
        List<MusicHistoryDTO> history  = postService.getMusicHistoryByMember(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(history, "회원의 음악 히스토리 조회가 완료되었습니다."));
    }
}
