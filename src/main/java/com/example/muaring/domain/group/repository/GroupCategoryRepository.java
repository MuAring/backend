package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.GroupCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupCategoryRepository extends JpaRepository<GroupCategory, Long> {
    Optional<GroupCategory> findByName(String name);
}