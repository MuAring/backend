package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.GroupCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCategoryRepository extends JpaRepository<GroupCategory, Long> {
    boolean existsByName(String name);
}