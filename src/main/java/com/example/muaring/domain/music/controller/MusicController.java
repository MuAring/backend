package com.example.muaring.domain.music.controller;

import com.example.muaring.domain.music.dto.MusicResponseDTO;
import com.example.muaring.domain.music.service.MusicService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    @GetMapping("/search")
    public ResponseEntity<List<MusicResponseDTO>> searchMusic(@RequestParam String query) {
        List<MusicResponseDTO> result = musicService.searchMusic(query);
        return ResponseEntity.ok(result);
    }
}