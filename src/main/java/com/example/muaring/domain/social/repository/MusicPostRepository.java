package com.example.muaring.domain.social.repository;

import com.example.muaring.domain.social.entity.MusicPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
