package com.example.muaring.domain.group.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.group.dto.GroupCreateRequestDto;
import com.example.muaring.domain.group.dto.GroupCreateResponseDto;
import com.example.muaring.domain.group.dto.GroupListResponseDto;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
            return ResponseEntity.status(401)
                    .body(ApiResponse.fail(GroupErrorCode.NULL_MEMBER,null));
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
}
