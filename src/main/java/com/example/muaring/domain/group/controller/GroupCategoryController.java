package com.example.muaring.domain.group.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.group.dto.GroupCategoryResponseDto;
import com.example.muaring.domain.group.entity.GroupCategoryType;
import com.example.muaring.domain.group.service.GroupCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/group-categories")
@RequiredArgsConstructor
public class GroupCategoryController {

    private final GroupCategoryService groupCategoryService;

    // [GET] /group-categories
    @GetMapping
    public ResponseEntity<ApiResponse<List<GroupCategoryResponseDto>>> getGroupCategories() {
        List<GroupCategoryResponseDto> categories = groupCategoryService.findAllCategories();

        ApiResponse<List<GroupCategoryResponseDto>> body =
                ApiResponse.ok(categories, "그룹 카테고리 목록 조회에 성공했습니다.");

        return ResponseEntity.ok(body);
    }

    // [GET] /group-categories/seeds
    @GetMapping("/seeds")
    public ResponseEntity<ApiResponse<List<GroupCategoryResponseDto>>> getGroupCategoriesForSeeds() {
        List<GroupCategoryResponseDto> seeds = Arrays.stream(GroupCategoryType.values())
                .map(GroupCategoryResponseDto::from)
                .toList();

        ApiResponse<List<GroupCategoryResponseDto>> body =
                ApiResponse.ok(seeds, "초기 시드용 그룹 카테고리 조회에 성공했습니다.");

        return ResponseEntity.ok(body);
    }
}
