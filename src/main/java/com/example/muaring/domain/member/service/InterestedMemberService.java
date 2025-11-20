package com.example.muaring.domain.member.service;

import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.member.entity.InterestedMember;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.InterestedMemberRepository;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InterestedMemberService {

    private final MemberRepository memberRepository;
    private final InterestedMemberRepository interestedMemberRepository;

    public void addInterest(Long followeeId) {

        Long followerId = SecurityUtil.getMemberId();

        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOWER_NOT_FOUND));

        Member followee = memberRepository.findById(followeeId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOWEE_NOT_FOUND));

        if (followerId.equals(followeeId)) {
            throw new MemberException(MemberErrorCode.MEMBER_CONFLICT);
        }

        if (interestedMemberRepository.existsByFollowerAndFollowee(follower, followee)) {
            throw new MemberException(MemberErrorCode.ALREADY_INTERESTED);
        }

        InterestedMember interest = InterestedMember.builder()
                .follower(follower)
                .followee(followee)
                .createdAt(LocalDateTime.now())
                .build();

        interestedMemberRepository.save(interest);
    }

    public void removeInterest(Long followeeId) {

        Long followerId = SecurityUtil.getMemberId();

        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOWER_NOT_FOUND));

        Member followee = memberRepository.findById(followeeId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOWEE_NOT_FOUND));

        InterestedMember interest = interestedMemberRepository
                .findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INTEREST_NOT_FOUND));

        interestedMemberRepository.delete(interest);
    }
}
