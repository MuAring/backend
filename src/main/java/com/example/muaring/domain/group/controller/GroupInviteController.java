package com.example.muaring.domain.group.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.group.dto.GroupInviteResponseDto;
import com.example.muaring.domain.group.dto.InvitePreviewResponseDto;
import com.example.muaring.domain.group.service.GroupInviteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invites")
@RequiredArgsConstructor
public class GroupInviteController {

    private final GroupInviteService groupInviteService;

    // [POST] /invites/groups/{groupId}
    // 초대 링크 생성 (모든 그룹 멤버 가능)
    @PostMapping("/groups/{groupId}")
    public ResponseEntity<ApiResponse<GroupInviteResponseDto>> createInviteToken(
            @PathVariable Long groupId) {

        GroupInviteResponseDto response = groupInviteService.createInviteToken(
                groupId,
                SecurityUtil.getMemberId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "초대 링크가 생성되었습니다."));
    }

    // [GET] /invites/groups/{groupId}
    // 활성화된 초대 링크 목록 조회
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<ApiResponse<List<GroupInviteResponseDto>>> getActiveInviteTokens(
            @PathVariable Long groupId) {

        List<GroupInviteResponseDto> response = groupInviteService.getActiveInviteTokens(
                groupId,
                SecurityUtil.getMemberId()
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(response));
    }

    // [DELETE] /invites/groups/{groupId}/{inviteId}
    // 초대 링크 삭제
    @DeleteMapping("/groups/{groupId}/{inviteId}")
    public ResponseEntity<ApiResponse<Void>> deleteInviteToken(
            @PathVariable Long groupId,
            @PathVariable Long inviteId) {

        groupInviteService.deleteInviteToken(groupId, inviteId, SecurityUtil.getMemberId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok("초대 링크가 삭제되었습니다."));
    }


    // [GET] /invites/{inviteToken}
    // 초대 링크 미리보기
    @GetMapping("/preview/{inviteToken}")
    public ResponseEntity<ApiResponse<InvitePreviewResponseDto>> getInvitePreview(
            @PathVariable String inviteToken) {

        InvitePreviewResponseDto response = groupInviteService.getInvitePreview(inviteToken);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(response));
    }

    // [POST] /invites/{inviteToken}/join
    // 초대 링크로 그룹 가입
    @PostMapping("/{inviteToken}/join")
    public ResponseEntity<ApiResponse<Void>> joinByInviteToken(
            @PathVariable String inviteToken) {

        groupInviteService.joinByInviteToken(inviteToken, SecurityUtil.getMemberId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok("그룹에 가입되었습니다."));
    }
}