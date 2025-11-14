package com.example.muaring.domain.group.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.auth.exception.AuthErrorCode;
import com.example.muaring.domain.auth.security.MemberPrincipal;
import com.example.muaring.domain.group.dto.*;
import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.group.dto.GroupCreateRequestDto;
import com.example.muaring.domain.group.dto.GroupCreateResponseDto;
import com.example.muaring.domain.group.dto.GroupListResponseDto;
import com.example.muaring.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<GroupCreateResponseDto>> createGroup(
            @RequestBody GroupCreateRequestDto requestDto) {
        Long adminId = SecurityUtil.getMemberId();
        if (adminId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(AuthErrorCode.UNAUTHORIZED_MEMBER,null));
        }

        GroupCreateResponseDto responseDto = groupService.createGroup(requestDto, adminId);
        Long newGroupId = responseDto.getGroupId();

        URI location = URI.create("/groups/" + newGroupId);

        ApiResponse<GroupCreateResponseDto> apiResponse =
                ApiResponse.created(responseDto, "그룹이 성공적으로 생성되었습니다.");

        return ResponseEntity.created(location).body(apiResponse);
    }

    /**
     * [GET] /groups?isPublic
     * 그룹 목록 동적 조회 (검색, 필터링, 페이지네이션)
     * q, isPublic, categoryIds
     */
    @GetMapping
    public ResponseEntity<ApiResponse<GroupListResponseDto>> getPublicGroups(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isPublic,
            @RequestParam(required = false) List<Long> categoryIds,
            @PageableDefault(size = 10, sort = "createdAt", // 추후 필요에 따라 설정 변경
                    direction = Sort.Direction.DESC) Pageable pageable
    ) {
        boolean onlyPublic = isPublic == null || isPublic;

        GroupListResponseDto response = groupService.getGroups(
                name,
                onlyPublic,
                categoryIds,
                pageable.getPageNumber(), // 0-based page
                pageable.getPageSize(),   // size
                pageable.getSort()        // sort
        );

        ApiResponse<GroupListResponseDto> body = ApiResponse.ok(response, "그룹 리스트 조회에 성공했습니다.");
        return ResponseEntity.ok(body);
    }


    // [GET] /groups/{groupId}/members
    // 그룹 멤버 목록 조회
    @GetMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<List<GroupMemberResponseDto>>> getGroupMembers(
            @PathVariable Long groupId) {
        Long memberId = SecurityUtil.getMemberId();
        List<GroupMemberResponseDto> members = groupService.getGroupMembers(groupId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(members, "그룹 멤버 목록을 조회했습니다."));
    }


    // [PATCH] /groups/{groupId}
    // 그룹 정보 수정 (그룹 이름, 최대 멤버, 설명, 공개여부, 카테고리)
    @PatchMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupUpdateResponseDto>> updateGroup(
            @PathVariable Long groupId,
            @RequestBody GroupUpdateRequestDto request) {
        Long memberId = SecurityUtil.getMemberId();
        GroupUpdateResponseDto response = groupService.updateGroup(groupId, memberId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(response, "그룹 정보가 수정되었습니다."));
    }

    // 그룹 프로필 이미지 수정


    // [DELETE] /groups/{groupId}
    // 그룹 삭제
    @DeleteMapping("/{groupId}")
    public ResponseEntity<ApiResponse<Void>> deleteGroup(
            @PathVariable Long groupId) {
        Long memberId = SecurityUtil.getMemberId();
        groupService.deleteGroup(groupId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok("그룹이 삭제되었습니다."));
    }


    // [DELETE] /groups/{groupId}/leave
    // 그룹 탈퇴
    @DeleteMapping("/{groupId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveGroup(
            @PathVariable Long groupId) {
        Long memberId = SecurityUtil.getMemberId();
        groupService.leaveGroup(groupId, memberId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok("그룹에서 탈퇴했습니다."));
    }

    // [POST] /groups/{groupId}/admin-leave
    // 관리자 그룹 탈퇴
    @PostMapping("/{groupId}/admin-leave")
    public ResponseEntity<ApiResponse<Void>> adminLeaveGroup(
            @PathVariable Long groupId,
            @RequestBody AdminLeaveRequestDto request) {

        Long memberId = SecurityUtil.getMemberId();;
        groupService.adminLeaveGroup(groupId, memberId, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok("관리자 권한을 양도하고 그룹에서 탈퇴했습니다."));
    }

    // [DELETE] /groups/{groupId}/members/{expellerId}
    // 그룹 멤버 추방
    @DeleteMapping("/{groupId}/members/{expellerId}")
    public ResponseEntity<ApiResponse<Void>> expelMember(
            @PathVariable Long groupId,
            @PathVariable Long expellerId) {

        Long adminId = SecurityUtil.getMemberId();
        groupService.expelMember(groupId, adminId, expellerId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok("그룹 멤버 추방을 완료했습니다."));
    }

}
