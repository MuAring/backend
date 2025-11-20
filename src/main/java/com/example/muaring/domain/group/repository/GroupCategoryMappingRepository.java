package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupCategoryMapping;
import com.example.muaring.domain.group.repository.projection.GroupIdCategoryIdProjection;
import com.example.muaring.domain.group.repository.projection.GroupIdCategoryNameProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupCategoryMappingRepository extends JpaRepository<GroupCategoryMapping, Long> {

    // 그룹 ID를 활용해 category ID들을 받아오는 쿼리
    @Query("""
        select gcm.group.id as groupId,
               gcm.groupCategory.id as groupCategoryId
        from GroupCategoryMapping gcm
        where gcm.group.id in :groupIds
    """)
    List<GroupIdCategoryIdProjection> findPairsByGroupIds(@Param("groupIds") List<Long> groupIds);

    @Query("""
    select 
        gm.group.id as groupId,
        gc.name as categoryCode
    from GroupCategoryMapping gm
    join gm.groupCategory gc
    where gm.group.id in :groupIds
""")
    List<GroupIdCategoryNameProjection> findPairsWithNamesByGroupIds(@Param("groupIds") List<Long> groupIds);

    List<GroupCategoryMapping> findByGroup(Group group);
    void deleteByGroup(Group group);

}
