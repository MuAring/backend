package com.example.muaring.domain.group.service;

import com.example.muaring.domain.group.dto.GroupCategoryResponseDto;
import com.example.muaring.domain.group.entity.GroupCategory;
import com.example.muaring.domain.group.repository.GroupCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupCategoryService {

    private final GroupCategoryRepository groupCategoryRepository;

    public List<GroupCategoryResponseDto> findAllCategories() {
        List<GroupCategory> entities = groupCategoryRepository.findAll();
        return entities.stream()
                .map(GroupCategoryResponseDto::from)
                .toList();
    }
}
