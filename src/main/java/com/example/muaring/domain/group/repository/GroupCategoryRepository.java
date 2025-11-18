package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.GroupCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupCategoryRepository extends JpaRepository<GroupCategory, Long> {
    Optional<GroupCategory> findByName(String name);

    @Query("select gcm.groupCategory.id from GroupCategoryMapping gcm where gcm.group.id = :groupId")
    List<Long> findCategoryIdsByGroupId(@Param("groupId") Long groupId);
}