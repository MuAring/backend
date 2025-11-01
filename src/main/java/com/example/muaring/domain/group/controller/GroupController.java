package com.example.muaring.domain.group.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.auth.security.MemberPrincipal;
import com.example.muaring.domain.group.dto.*;
import com.example.muaring.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    // [POST] /groups
    @PostMapping
    public ResponseEntity<GroupCreateResponseDto> createGroup(@RequestBody GroupCreateRequestDto requestDto) {
        GroupCreateResponseDto responseDto = groupService.createGroup(requestDto);
        Long newGroupId = responseDto.getGroupId();

        URI location = URI.create("/groups/" + newGroupId);

        return ResponseEntity.created(location).body(responseDto);
    }

    /**
     * [GET] /groups?isPublic
     * 그룹 목록 동적 조회 (검색, 필터링, 페이지네이션)
     * q, isPublic, categoryIds
     */
    @GetMapping
    public ResponseEntity<GroupListResponseDto> getPublicGroups(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) List<Long> categoryIds,
            @PageableDefault(size = 10, sort = "createdAt", // 추후 필요에 따라 설정 변경
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        GroupListResponseDto response = groupService.getGroups(
                q,
                isPublic,
                categoryIds,
                pageable.getPageNumber(), // 0-based page
                pageable.getPageSize(),   // size
                pageable.getSort()        // sort
        );

        return ResponseEntity.ok(response);
    }


    // [GET] /groups/{groupId}/members
    // 그룹 멤버 목록 조회
    @GetMapping("/{groupId}/members")
    public ApiResponse<List<GroupMemberResponseDto>> getGroupMembers(
            @PathVariable Long groupId,
            //@AuthenticationPrincipal MemberPrincipal principal
            Long memberId) {
        //Long memberId = principal.getMemberId();
        List<GroupMemberResponseDto> members = groupService.getGroupMembers(groupId, memberId);
        return ApiResponse.ok(members, "그룹 멤버 목록을 조회했습니다.");
    }


    // [PATCH] /groups/{groupId}
    // 그룹 정보 수정 (그룹 이름, 최대 멤버, 설명, 공개여부, 카테고리)
    @PatchMapping("/{groupId}")
    public ApiResponse<GroupUpdateResponseDto> updateGroup(
            @PathVariable Long groupId,
            Long memberId,
            //@AuthenticationPrincipal MemberPrincipal principal,
            @RequestBody GroupUpdateRequestDto request) {
        //Long memberId = principal.getMemberId();
        GroupUpdateResponseDto response = groupService.updateGroup(groupId, memberId, request);
        return ApiResponse.ok(response, "그룹 정보가 수정되었습니다.");
    }

    // 그룹 프로필 이미지 수정
}
