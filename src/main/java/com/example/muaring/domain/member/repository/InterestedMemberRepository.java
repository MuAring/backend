package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.member.entity.InterestedMember;
import com.example.muaring.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestedMemberRepository  extends JpaRepository<InterestedMember, Long> {

    Optional<InterestedMember> findByFollowerAndFollowee(Member follower, Member followee);

    boolean existsByFollowerAndFollowee(Member follower, Member followee);

}
