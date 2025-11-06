package com.example.muaring.domain.social.repository;

import com.example.muaring.domain.social.entity.MusicPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
