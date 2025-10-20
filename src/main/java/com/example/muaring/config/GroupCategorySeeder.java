package com.example.muaring.config;

import com.example.muaring.domain.group.entity.GroupCategory;
import com.example.muaring.domain.group.entity.GroupCategoryType;
import com.example.muaring.domain.group.repository.GroupCategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GroupCategorySeeder {

    private final GroupCategoryRepository repo;

    @PostConstruct
    @Transactional
    public void init() {
        for (GroupCategoryType type : GroupCategoryType.values()) {
            if (!repo.existsByName(type.getName())) {
                repo.save(new GroupCategory(type.getName()));
            }
        }
    }
}