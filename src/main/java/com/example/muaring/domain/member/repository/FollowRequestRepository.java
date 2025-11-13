package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.member.entity.FollowRequest;
import com.example.muaring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    Optional<FollowRequest> findByFollowerAndFollowee(Member follower, Member followee);

}
