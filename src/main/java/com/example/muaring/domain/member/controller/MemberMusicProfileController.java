package com.example.muaring.domain.member.controller;

import com.example.muaring.domain.member.dto.request.MemberMusicProfileRequestDto;
import com.example.muaring.domain.member.dto.response.MemberMusicProfileResponseDto;
import com.example.muaring.domain.member.service.MemberMusicProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/{userId}/music-preferences")
@RequiredArgsConstructor
public class MemberMusicProfileController {

    private final MemberMusicProfileService memberMusicProfileService;

    /** 조회 (없으면 자동 계산) */
    @GetMapping
    public ResponseEntity<MemberMusicProfileResponseDto> getUserProfile(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                memberMusicProfileService.getOrRecalculate(userId)
        );
    }

    /** 강제 재계산 */
    @PutMapping
    public ResponseEntity<MemberMusicProfileResponseDto> recalcUserProfile(
            @PathVariable Long userId,
            @RequestBody(required = false) MemberMusicProfileRequestDto request
    ) {
        return ResponseEntity.ok(
                memberMusicProfileService.recalculate(userId)
        );
    }
}
