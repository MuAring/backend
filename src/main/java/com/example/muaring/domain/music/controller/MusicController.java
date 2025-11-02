package com.example.muaring.domain.music.controller;

import com.example.muaring.domain.music.dto.MusicPostRequestDTO;
import com.example.muaring.domain.music.dto.MusicResponseDTO;
import com.example.muaring.domain.music.service.MusicPostService;
import com.example.muaring.domain.social.entity.MusicPost;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicPostService musicPostService;

    @GetMapping("/search")
    public ResponseEntity<List<MusicResponseDTO>> searchMusic(@RequestParam String query) {
        List<MusicResponseDTO> result = musicPostService.searchMusic(query);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<MusicPost> createMusicPost(@RequestBody MusicPostRequestDTO request) {
        MusicPost post = musicPostService.createMusicPost(
                request.getMemberId(),
                request.getGroupId(),
                request.getMusicId(),
                request.getContent()
        );
        return ResponseEntity.ok(post);
    }
}