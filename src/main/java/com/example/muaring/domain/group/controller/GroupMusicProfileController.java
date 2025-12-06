package com.example.muaring.domain.group.controller;

import com.example.muaring.domain.group.dto.GroupMusicProfileRequestDto;
import com.example.muaring.domain.group.dto.GroupMusicProfileResponseDto;
import com.example.muaring.domain.group.service.GroupMusicProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups/{groupId}/music-profile")
@RequiredArgsConstructor
public class GroupMusicProfileController {

    private final GroupMusicProfileService groupMusicProfileService;

    /**
     * 그룹 음악 프로필 조회
     * - 없으면 계산해서 생성 후 반환
     */
    @GetMapping
    public ResponseEntity<GroupMusicProfileResponseDto> getGroupMusicProfile(
            @PathVariable Long groupId
    ) {
        GroupMusicProfileResponseDto response =
                groupMusicProfileService.getOrRecalculateProfile(groupId);
        return ResponseEntity.ok(response);
    }

    /**
     * 그룹 음악 프로필 강제 재계산 (관리자/내부 용도)
     */
    @PostMapping("/recalculate")
    public ResponseEntity<GroupMusicProfileResponseDto> recalculateGroupMusicProfile(
            @PathVariable Long groupId,
            @RequestBody(required = false) GroupMusicProfileRequestDto request
    ) {
        GroupMusicProfileResponseDto response =
                groupMusicProfileService.recalculateProfile(groupId);
        return ResponseEntity.ok(response);
    }
}
