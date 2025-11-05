package com.example.muaring.domain.music.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import com.example.muaring.domain.music.dto.MusicPostRequestDTO;
import com.example.muaring.domain.music.dto.SpotifyTrackDTO;
import com.example.muaring.domain.social.service.MusicPostService;
import com.example.muaring.domain.social.entity.MusicPost;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicPostService musicPostService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SpotifyTrackDTO>>> searchMusic(@RequestParam String query) {
        List<SpotifyTrackDTO> result = musicPostService.searchMusic(query);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(result, "음악 검색이 완료되었습니다."));
    }

    @GetMapping("/history/{memberId}")
    public ResponseEntity<ApiResponse<List<MusicHistoryDTO>>> getMusicHistoryByMember(@PathVariable Long memberId) {
        List<MusicHistoryDTO> history  = musicPostService.getMusicHistoryByMember(memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(history, "회원의 음악 히스토리 조회가 완료되었습니다."));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MusicPost>> createMusicPost(@RequestBody MusicPostRequestDTO request) {
        MusicPost post = musicPostService.createMusicPost(
                request.getMemberId(),
                request.getGroupId(),
                request.getMusicId(),
                request.getContent()
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(post, "음악 게시글이 작성되었습니다."));
    }
}