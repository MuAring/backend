package com.example.muaring.domain.member.service;

import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.member.dto.FollowMemberListDTO;
import com.example.muaring.domain.member.dto.response.FollowResponseDTO;
import com.example.muaring.domain.member.entity.Follow;
import com.example.muaring.domain.member.entity.FollowRequest;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.exception.MemberException;
import com.example.muaring.domain.member.repository.FollowRepository;
import com.example.muaring.domain.member.repository.FollowRequestRepository;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.member.response.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final MemberRepository memberRepository;

    public FollowResponseDTO sendFollowRequest(Long followeeId) {

        Long followerId = SecurityUtil.getMemberId();

        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOWER_NOT_FOUND));
        Member followee = memberRepository.findById(followeeId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOWEE_NOT_FOUND));

        if(Objects.equals(followerId, followeeId)) {
            throw new MemberException(MemberErrorCode.MEMBER_CONFLICT);
        }
        if (followRepository.findByFollowerAndFollowee(follower, followee).isPresent()) {
            throw new MemberException(MemberErrorCode.FOLLOW_ALREADY_EXISTS);
        }

        if (followRequestRepository.findByFollowerAndFollowee(follower, followee).isPresent()) {
            throw new MemberException(MemberErrorCode.FOLLOW_REQUEST_ALREADY_EXISTS);
        }

        if (followee.getIsPublic()) {
            Follow follow = Follow.builder()
                    .follower(follower)
                    .followee(followee)
                    .createdAt(LocalDateTime.now())
                    .build();
            followRepository.save(follow);

            return FollowResponseDTO.builder()
                    .followId(follow.getId())
                    .followerId(follower.getId())
                    .followeeId(followee.getId())
                    .status("APPROVED")
                    .createdAt(follow.getCreatedAt())
                    .build();
        }

        FollowRequest request = FollowRequest.builder()
                .follower(follower)
                .followee(followee)
                .status(FollowRequest.FollowRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        followRequestRepository.save(request);

        return FollowResponseDTO.builder()
                .followId(request.getId())
                .followerId(follower.getId())
                .followeeId(followee.getId())
                .status(request.getStatus().name())
                .createdAt(request.getCreatedAt())
                .build();
    }

    public FollowResponseDTO approveFollowRequest(Long requestId) {

        Long followeeId = SecurityUtil.getMemberId();

        FollowRequest request = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOW_REQUEST_NOT_FOUND));

        if (!request.getFollowee().getId().equals(followeeId)) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACTION);
        }

        if (request.getStatus() == FollowRequest.FollowRequestStatus.APPROVED) {
            throw new MemberException(MemberErrorCode.FOLLOW_ALREADY_EXISTS);
        }

        request.updateStatus(FollowRequest.FollowRequestStatus.APPROVED);

        Follow follow = Follow.builder()
                .follower(request.getFollower())
                .followee(request.getFollowee())
                .createdAt(LocalDateTime.now())
                .build();

        followRepository.save(follow);
        followRequestRepository.save(request);

        return FollowResponseDTO.builder()
                .followId(follow.getId())
                .followerId(request.getFollower().getId())
                .followeeId(request.getFollowee().getId())
                .status(request.getStatus().name())
                .createdAt(follow.getCreatedAt())
                .build();
    }

    public FollowResponseDTO rejectFollowRequest(Long requestId) {

        Long followeeId = SecurityUtil.getMemberId();

        FollowRequest request = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOW_REQUEST_NOT_FOUND));

        if (!request.getFollowee().getId().equals(followeeId)) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACTION);
        }

        if (request.getStatus() == FollowRequest.FollowRequestStatus.APPROVED) {
            throw new MemberException(MemberErrorCode.FOLLOW_ALREADY_EXISTS);
        }

        request.updateStatus(FollowRequest.FollowRequestStatus.REJECTED);
        followRequestRepository.save(request);

        return FollowResponseDTO.builder()
                .followId(request.getId())
                .followerId(request.getFollower().getId())
                .followeeId(request.getFollowee().getId())
                .status(request.getStatus().name())
                .createdAt(request.getCreatedAt())
                .build();
    }

    public void unfollow(Long followeeId) {

        Long followerId = SecurityUtil.getMemberId();

        if (Objects.equals(followerId, followeeId)) {
            throw new MemberException(MemberErrorCode.MEMBER_CONFLICT);
        }

        Member follower = memberRepository.findById(followerId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Member followee = memberRepository.findById(followeeId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOWEE_NOT_FOUND));

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee)
                .orElseThrow(() -> new MemberException(MemberErrorCode.FOLLOWEE_NOT_FOUND));

        followRepository.delete(follow);
    }

    public List<FollowMemberListDTO> getFollowers(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Follow> followers = followRepository.findAllByFollowee(member);

        return followers.stream()
                .map(follow -> {
                    Member follower = follow.getFollower();
                    return FollowMemberListDTO.builder()
                            .memberId(follower.getId())
                            .name(follower.getNickname())
                            .profileImage(
                                    follower.getProfileImage() != null
                                            ? follower.getProfileImage().getUrl()  // 실제 필드명에 맞게 변경 (예: getFilePath())
                                            : null
                            )                            .isPublic(follower.getIsPublic())
                            .followStatus("FOLLOWER")
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<FollowMemberListDTO> getFollowings(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Follow> followings = followRepository.findAllByFollower(member);

        return followings.stream()
                .map(follow -> {
                    Member followee = follow.getFollowee();
                    return FollowMemberListDTO.builder()
                            .memberId(followee.getId())
                            .name(followee.getNickname())
                            .profileImage(
                                    followee.getProfileImage() != null
                                            ? followee.getProfileImage().getUrl()  // 실제 필드명에 맞게 변경 (예: getFilePath())
                                            : null
                            )                              .isPublic(followee.getIsPublic())
                            .followStatus("FOLLOWING")
                            .build();
                })
                .collect(Collectors.toList());
    }
}
