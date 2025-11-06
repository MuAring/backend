package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /** 검색어, 공개 여부, 카테고리를 필터링(옵션 필터)해서 처리하는 메서드
     * -1) 공개 여부 필터: isPublic 파라미터가 null이면 둘 다 가져옴
     * -2) 검색어 필터: name이 빈 문자열이면 검색어 조건 X (넣으면 name 기준 검색)
     * -3) 카테고리 필터: categoryIds가 null이면 조건 X -> 매핑 테이블에 존재 여부 체크
     */
    @Query(
            value = """
            select g
            from Group g
            where g.isDeleted = false
              and (:isPublic is null or g.isPublic = :isPublic)
              and (:name is null or :name = ''
                   or lower(g.name) like lower(concat('%', :name, '%')))
              and (coalesce(:categoryIds, null) is null
                   or exists (
                       select 1
                       from GroupCategoryMapping m
                       where m.group = g
                         and m.groupCategory.id in :categoryIds
                   ))
            """,
            countQuery = """
            select count(g)
            from Group g
            where g.isDeleted = false
              and (:isPublic is null or g.isPublic = :isPublic)
              and (:name is null or :name = ''
                   or lower(g.name) like lower(concat('%', :name, '%')))
              and (coalesce(:categoryIds, null) is null
                   or exists (
                       select 1
                       from GroupCategoryMapping m
                       where m.group = g
                         and m.groupCategory.id in :categoryIds
                   ))
            """
    )
    Page<Group> search(
            @Param("name") String name,
            @Param("isPublic") Boolean isPublic,
            @Param("categoryIds") List<Long> categoryIds,
            Pageable pageable
    );

    // id로 검색할 때, 삭제되지 않은 그룹만 반환
    @Query("SELECT g FROM Group g WHERE g.id = :id AND g.isDeleted = false")
    Optional<Group> findById(@Param("id") Long id);
}
