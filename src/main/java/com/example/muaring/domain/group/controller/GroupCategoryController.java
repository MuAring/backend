package com.example.muaring.domain.group.controller;

import com.example.muaring.domain.group.dto.GroupCategoryResponseDto;
import com.example.muaring.domain.group.entity.GroupCategoryType;
import com.example.muaring.domain.group.service.GroupCategoryService;
import lombok.RequiredArgsConstructor;
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
    public List<GroupCategoryResponseDto> getGroupCategories() {
        return groupCategoryService.findAllCategories();
    }

    // [GET] /group-categories/seeds
    @GetMapping("/seeds")
    public List<GroupCategoryResponseDto> getGroupCategoriesForSeeds() {
        return Arrays.stream(GroupCategoryType.values())
                .map(GroupCategoryResponseDto::from)
                .toList();
    }
}
