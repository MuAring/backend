package com.example.muaring.domain.group.controller;

import com.example.muaring.domain.group.dto.GroupCreateRequestDto;
import com.example.muaring.domain.group.dto.GroupCreateResponseDto;
import com.example.muaring.domain.group.dto.GroupListResponseDto;
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
}
