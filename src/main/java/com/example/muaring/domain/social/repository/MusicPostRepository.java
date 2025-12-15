package com.example.muaring.domain.social.repository;

import com.example.muaring.domain.social.entity.MusicPost;
import com.example.muaring.domain.social.repository.projection.MemberTodayMusicProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MusicPostRepository extends JpaRepository<MusicPost, Long> {

    @Query("""
        SELECT mp
        FROM MusicPost mp
        WHERE mp.member.id = :memberId
          AND mp.group IS NULL
          AND YEAR(mp.createdAt) = :year
          AND MONTH(mp.createdAt) = :month
        ORDER BY mp.createdAt ASC
    """)
    Page<MusicPost> findByMemberAndYearMonth(
            @Param("memberId") Long memberId,
            @Param("year") Integer year,
            @Param("month") Integer month,
            Pageable pageable
    );

    // 그룹 히스토리용: 한 달 동안의 그룹 포스트 전체 (삭제되지 않은 것만), 시간순
    List<MusicPost> findByGroup_IdAndIsDeletedFalseAndCreatedAtBetweenOrderByCreatedAtAsc(
            Long groupId,
            LocalDateTime start,
            LocalDateTime end
    );

//    @Query(value = """
//        SELECT *
//        FROM music_post mp
//        WHERE mp.member_id IN (
//            SELECT f.followee_id
//            FROM follow f
//            WHERE f.follower_id = :memberId
//        )
//        AND mp.created_at >= CURRENT_DATE
//        AND mp.created_at < CURRENT_DATE + INTERVAL '1 day'
//        ORDER BY mp.created_at DESC
//        """, nativeQuery = true)
//    List<MusicPost> findTodayPostsByFollowees(@Param("memberId") Long memberId);

//    @Query(value = """
//        SELECT *
//        FROM music_post mp
//        WHERE mp.member_id IN (
//            SELECT f.followee_id
//            FROM follow f
//            WHERE f.follower_id = :memberId
//        )
//        AND mp.created_at >= CURRENT_DATE
//        AND mp.created_at < CURRENT_DATE + INTERVAL '1 day'
//        AND mp.group_id IS NULL
//        ORDER BY mp.created_at DESC
//        """,
//            countQuery = """
//        SELECT COUNT(*)
//        FROM music_post mp
//        WHERE mp.member_id IN (
//            SELECT f.followee_id
//            FROM follow f
//            WHERE f.follower_id = :memberId
//        )
//        AND mp.created_at >= CURRENT_DATE
//        AND mp.created_at < CURRENT_DATE + INTERVAL '1 day'
//        AND mp.group_id IS NULL
//        """,
//            nativeQuery = true)
//    Page<MusicPost> findTodayPostsByFollowees(
//            @Param("memberId") Long memberId,
//            Pageable pageable
//    );

    @Query(value = """
        SELECT *
        FROM music_post mp
        WHERE (
            mp.member_id IN (
                SELECT f.followee_id
                FROM follow f
                WHERE f.follower_id = :memberId
            )
            OR mp.member_id = :memberId
        )
        AND mp.created_at >= CURRENT_DATE
        AND mp.created_at < CURRENT_DATE + INTERVAL '1 day'
        AND mp.group_id IS NULL
        ORDER BY mp.created_at DESC
        """,
            countQuery = """
        SELECT COUNT(*)
        FROM music_post mp
        WHERE (
            mp.member_id IN (
                SELECT f.followee_id
                FROM follow f
                WHERE f.follower_id = :memberId
            )
            OR mp.member_id = :memberId
        )
        AND mp.created_at >= CURRENT_DATE
        AND mp.created_at < CURRENT_DATE + INTERVAL '1 day'
        AND mp.group_id IS NULL
        """,
            nativeQuery = true)
    Page<MusicPost> findTodayPostsByFollowees(
            @Param("memberId") Long memberId,
            Pageable pageable
    );


    @Query(value = """
        SELECT *
        FROM music_post
        WHERE member_id = :memberId
          AND group_id IS NULL
          AND created_at >= CURRENT_DATE
          AND created_at < CURRENT_DATE + INTERVAL '1 day'
        ORDER BY created_at DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<MusicPost> findTodayPostByMember(@Param("memberId") Long memberId);


    long countByMemberIdAndGroupIsNullAndIsDeletedFalse(Long memberId);

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

    /**
     * 그룹 성향 계산용:
     * - 1. 특정 그룹에 속한 포스트
     * - 2. 최근 90일
     * - 3. 삭제되지 않은 것
     * - 4. music + music.feature 까지 한 번에 로딩
     */
    @Query("""
        SELECT DISTINCT mp
        FROM MusicPost mp
        JOIN FETCH mp.music m
        JOIN FETCH m.feature f
        LEFT JOIN FETCH m.genres mg
        LEFT JOIN FETCH mg.genre g
        WHERE mp.group.id = :groupId
          AND mp.createdAt >= :from
          AND mp.isDeleted = false
        """)
    List<MusicPost> findRecentGroupPostsWithFeatureAndGenres(
            @Param("groupId") Long groupId,
            @Param("from") LocalDateTime from
    );

    @Query("""
    SELECT DISTINCT mp
    FROM MusicPost mp
    JOIN FETCH mp.music m
    JOIN FETCH m.feature f
    LEFT JOIN FETCH m.genres mg
    LEFT JOIN FETCH mg.genre g
    WHERE mp.member.id = :memberId
      AND mp.createdAt >= :from
      AND mp.isDeleted = false
    ORDER BY mp.createdAt DESC
    """)
    List<MusicPost> findRecentUserPostsWithFeatureAndGenres(
            @Param("memberId") Long memberId,
            @Param("from") LocalDateTime from
    );

    // 특정 멤버의 가장 최신 게시물 조회
    Optional<MusicPost> findFirstByMemberIdOrderByCreatedAtDesc(Long memberId);

    // 여러 멤버의 최신 게시물을 한 번에 조회 (성능 최적화)
    @Query("SELECT p FROM MusicPost p " +
            "WHERE p.id IN (" +
            "  SELECT MAX(p2.id) FROM MusicPost p2 " +
            "  WHERE p2.member.id IN :memberIds " +
            "  AND p2.isProfile = true " +
            "  GROUP BY p2.member.id" +
            ") " +
            "ORDER BY p.createdAt DESC")
    List<MusicPost> findLatestPostsByMemberIds(@Param("memberIds") List<Long> memberIds);

    // 게시물 상세 조회 (연관 엔티티 fetch join으로 한 번에 조회)
    @Query("SELECT p FROM MusicPost p " +
            "JOIN FETCH p.member m " +
            "LEFT JOIN FETCH m.profileImage " +
            "JOIN FETCH p.music " +
            "LEFT JOIN FETCH p.group g " +
            "WHERE p.id = :postId")
    Optional<MusicPost> findByIdWithDetails(@Param("postId") Long postId);

    @Query("""
        select mp
        from MusicPost mp
        join fetch mp.music m
        where mp.member.id in :memberIds
          and mp.createdAt >= :startOfDay
          and mp.createdAt < :endOfDay
    """)
    List<MusicPost> findTodayPostsByMemberIds(
            @Param("memberIds") List<Long> memberIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    // 오늘의 음악을 조회하는 쿼리 (groupId==null)
    @Query("""
        select mp.member.id as memberId,
               m.name as musicName,
               m.artistName as artistName
        from MusicPost mp
        join mp.music m
        where mp.member.id in :memberIds
          and mp.group is null
          and mp.createdAt >= :startOfDay
          and mp.createdAt < :endOfDay
    """)
    List<MemberTodayMusicProjection> findTodayPersonalMusicByMemberIds(
            @Param("memberIds") List<Long> memberIds,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    // 그룹의 고유한 음악 목록 조회 (중복 제거, 최신순)
    @Query(value = """
    WITH unique_music AS (
        SELECT DISTINCT ON (mp.music_id)
            mp.post_id,
            mp.music_id,
            mp.created_at
        FROM music_post mp
        WHERE mp.group_id = :groupId
          AND mp.is_deleted = false
        ORDER BY mp.music_id, mp.created_at ASC
    )
    SELECT 
        um.post_id,
        um.music_id,
        m.name,
        m.artist_name,
        m.album_img_url,
        m.album_name,
        um.created_at
    FROM unique_music um
    JOIN music m ON um.music_id = m.music_id
    ORDER BY um.created_at DESC
    LIMIT :limit OFFSET :offset
    """,
            countQuery = """
    SELECT COUNT(DISTINCT mp.music_id)
    FROM music_post mp
    WHERE mp.group_id = :groupId
      AND mp.is_deleted = false
    """,
            nativeQuery = true)
    List<Object[]> findUniqueMusicArchiveRaw(
            @Param("groupId") Long groupId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
    SELECT COUNT(DISTINCT mp.music_id)
    FROM music_post mp
    WHERE mp.group_id = :groupId
      AND mp.is_deleted = false
    """,
            nativeQuery = true)
    long countUniqueMusicByGroupId(@Param("groupId") Long groupId);

}
