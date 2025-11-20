package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.member.entity.Follow;
import com.example.muaring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowee(Member follower, Member followee);

    List<Follow> findAllByFollower(Member follower);

    List<Follow> findAllByFollowee(Member followee);

    boolean existsByFollowerIdAndFolloweeId(Long loginMemberId, Long targetId);

    // followeeId 회원을 팔로우하는 사람 수 (탈퇴한 회원은 count에서 제외)
    @Query("""
        SELECT COUNT(f)
        FROM Follow f
        WHERE f.followee.id = :followeeId
            AND f.follower.isDeleted = false
    """)
    long countByFolloweeId(@Param("followeeId") Long followeeId);

    // followerId 회원이 팔로우하는 사람 수 (탈퇴한 회원은 count에서 제외)
    @Query("""
        SELECT COUNT(f)
        FROM Follow f
        WHERE f.follower.id = :followerId
            AND f.followee.isDeleted = false
    """)
    long countByFollowerId(@Param("followerId") Long followerId);
}
