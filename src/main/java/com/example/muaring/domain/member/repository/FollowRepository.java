package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.member.dto.FollowMemberListDTO;
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

    boolean existsByFollowerAndFollowee(Member follower, Member followee);

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

    @Query("select f.followee.id from Follow f where f.follower.id = :followerId")
    List<Long> findFolloweeIdsByFollowerId(@Param("followerId") Long followerId);

    @Query("""
    select new com.example.muaring.domain.member.dto.FollowMemberListDTO
               (
        followee.id,
        followee.nickname,
        profile.s3Key,
        followee.isPublic,
        'FOLLOWING',
        music.name,
        music.artistName
    )
    from Follow f
        join f.followee followee
        left join followee.profileImage profile
        
        left join MusicPost mp
            on mp.member = followee
           and mp.createdAt = (
                select max(mp2.createdAt)
                from MusicPost mp2
                where mp2.member = followee
           )
        left join mp.music music
    where f.follower.id = :memberId
    """)
    List<FollowMemberListDTO> findFollowingsWithRecentPost(
            @Param("memberId") Long memberId
    );


    @Query("""
    select new com.example.muaring.domain.member.dto.FollowMemberListDTO
                                                  (
        follower.id,
        follower.nickname,
        profile.s3Key,
        follower.isPublic,
        case
        when f2.id is not null then 'FOLLOWING'
        else 'FOLLOW_BACK'
        end,
        music.name,
        music.artistName
    )
    from Follow f
        join f.follower follower
        left join follower.profileImage profile

        left join Follow f2
            on f2.follower.id = :memberId
           and f2.followee = follower

        left join MusicPost mp
            on mp.member = follower
           and mp.createdAt = (
                select max(mp2.createdAt)
                from MusicPost mp2
                where mp2.member = follower
           )

       left join mp.music music
    where f.followee.id = :memberId
    """)
    List<FollowMemberListDTO> findFollowersWithRecentPost(
            @Param("memberId") Long memberId
    );
}