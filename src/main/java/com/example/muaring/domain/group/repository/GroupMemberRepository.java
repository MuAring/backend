package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.GroupMember;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    @EntityGraph(attributePaths = { "group" })
    @Query("SELECT gm FROM GroupMember gm WHERE gm.member.id = :memberId AND gm.isDeleted = false ORDER BY gm.group.name ASC")
    List<GroupMember> findByMember_IdOrderByGroup_NameAsc(Long memberId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.isDeleted = false")
    List<GroupMember> findByGroupId(Long groupId);

    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.member.id = :memberId AND gm.isDeleted = false")
    boolean existsByGroupIdAndMemberId(Long groupId, Long memberId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.member.id = :memberId AND gm.isDeleted = false")
    Optional<GroupMember> findByGroupIdAndMemberId(Long groupId, Long memberId);

    @Query("""
        SELECT COUNT(*)
        FROM GroupMember gm
        WHERE gm.member.id = :memberId
            AND gm.member.isDeleted = false
    """)
    long countByMemberId(@Param("memberId") Long memberId);

    // memberId로 해당 멤버가 가입한 그룹의 ID들만 조회
    @Query("select gm.group.id from GroupMember gm where gm.member.id = :memberId")
    List<Long> findGroupIdsByMemberId(@Param("memberId") Long memberId);

    // 닉네임으로 검색
    @Query("SELECT gm FROM GroupMember gm " +
            "JOIN FETCH gm.member m " +
            "WHERE gm.group.id = :groupId " +
            "AND m.nickname LIKE %:search% " +
            "AND gm.isDeleted = false " +
            "ORDER BY m.nickname ASC")
    List<GroupMember> findByGroupIdAndMemberNicknameContaining(
            @Param("groupId") Long groupId,
            @Param("search") String search);

    // 내가 소속된 그룹 중 이름으로 검색
    @EntityGraph(attributePaths = { "group" })
    @Query("SELECT gm FROM GroupMember gm " +
            "WHERE gm.member.id = :memberId " +
            "  AND gm.isDeleted = false " +
            "  AND LOWER(gm.group.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "ORDER BY gm.group.name ASC")
    List<GroupMember> findByMember_IdAndGroup_NameContainingIgnoreCaseOrderByGroup_NameAsc(
            Long memberId,
            String name
    );
}
