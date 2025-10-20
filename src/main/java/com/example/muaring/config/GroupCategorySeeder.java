package com.example.muaring.config;

import com.example.muaring.domain.group.entity.GroupCategory;
import com.example.muaring.domain.group.entity.GroupCategoryType;
import com.example.muaring.domain.group.repository.GroupCategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GroupCategorySeeder {

    private final GroupCategoryRepository repo;

    @PostConstruct
    @Transactional
    public void init() {
        // DB에서 한 번에 모든 카테고리 조회
        List<GroupCategory> existingCategories = repo.findAll();
        
        // Set으로 변환해 중복 (있다면) 제거
        Set<String> existingCategoryNames = existingCategories.stream()
                .map(GroupCategory::getName)
                .collect(Collectors.toSet());

        // DB에 없는 값 필터링해서 새로운 엔티티 리스트 생성
        List<GroupCategory> newCategories = Arrays.stream(GroupCategoryType.values())
                .filter(type -> !existingCategoryNames.contains(type.getName()))
                .map(type -> new GroupCategory(type.getName()))
                .collect(Collectors.toList());

        // 전부 추가
        if (!newCategories.isEmpty()) {
            repo.saveAll(newCategories);
        }
    }
}