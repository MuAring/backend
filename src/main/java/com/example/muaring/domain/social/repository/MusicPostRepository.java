package com.example.muaring.domain.social.repository;

import com.example.muaring.domain.social.entity.MusicPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MusicPostRepository extends JpaRepository<MusicPost, Long> {

    @Query("""
    SELECT mp
    FROM MusicPost mp
    WHERE mp.member.id = :memberId
      AND YEAR(mp.createdAt) = :year
      AND MONTH(mp.createdAt) = :month
    ORDER BY mp.createdAt DESC
    """)

    Page<MusicPost> findByMemberAndYearMonth(
            @Param("memberId") Long memberId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    @Query(value = """
        SELECT *
        FROM music_post mp
        WHERE mp.member_id IN (
            SELECT f.followee_id
            FROM follow f
            WHERE f.follower_id = :memberId
        )
        AND mp.created_at >= CURRENT_DATE
        AND mp.created_at < CURRENT_DATE + INTERVAL '1 day'
        ORDER BY mp.created_at DESC
        """, nativeQuery = true)

    List<MusicPost> findTodayPostsByFollowees(@Param("memberId") Long memberId);

    long countByMemberIdAndIsDeletedIsFalse( Long memberId);

    @Query("select count(mp) from MusicPost mp where mp.group.id = :groupId and mp.isDeleted = false")
    int countActiveByGroupId(@Param("groupId") Long groupId);

    // 전체 조회 (그룹 기준)
    Page<MusicPost> findByGroup_Id(Long groupId, Pageable pageable);

    // 특정 월 범위 조회
    Page<MusicPost> findByGroup_IdAndCreatedAtBetween(
            Long groupId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(p) > 0
        FROM MusicPost p
        WHERE p.member.id = :memberId
          AND p.createdAt >= :startOfDay
          AND p.createdAt < :endOfDay
    """)
    boolean existsTodayPostByMember(@Param("memberId") Long memberId,
                                    @Param("startOfDay") LocalDateTime startOfDay,
                                    @Param("endOfDay") LocalDateTime endOfDay);
}
