package com.example.muaring.domain.music.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.music.service.MusicService;
import com.example.muaring.domain.music.dto.SpotifyTrackDTO;

import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SpotifyTrackDTO>>> searchMusic(@RequestParam String query) {
        List<SpotifyTrackDTO> result = musicService.searchMusic(query);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(result, "음악 검색이 완료되었습니다."));
    }
}