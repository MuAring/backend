package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.member.entity.Follow;
import com.example.muaring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    Optional<Follow> findByFollowerAndFollowee(Member follower, Member followee);

    List<Follow> findAllByFollower(Member follower);

    List<Follow> findAllByFollowee(Member followee);

}
