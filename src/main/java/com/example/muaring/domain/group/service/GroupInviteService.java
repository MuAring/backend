package com.example.muaring.domain.group.service;

import com.example.muaring.domain.group.dto.GroupInviteResponseDto;
import com.example.muaring.domain.group.dto.InvitePreviewResponseDto;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.group.entity.GroupInviteToken;
import com.example.muaring.domain.group.entity.GroupMember;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.repository.GroupInviteTokenRepository;
import com.example.muaring.domain.group.repository.GroupMemberRepository;
import com.example.muaring.domain.group.repository.GroupRepository;
import com.example.muaring.domain.group.response.GroupException;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupInviteService {

    private final GroupInviteTokenRepository inviteTokenRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRepository memberRepository;

    @Value("${app.base-url}")
    private String baseUrl;


    @PostConstruct  // ✅ 서버 시작 시 baseUrl 출력
    public void init() {
        System.out.println("========================================");
        System.out.println("BASE_URL 설정값: " + baseUrl);
        System.out.println("========================================");
    }

    // 초대 링크 생성
    @Transactional
    public GroupInviteResponseDto createInviteToken(Long groupId, Long memberId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.GROUP_NOT_FOUND));

        GroupMember groupMember = groupMemberRepository
                .findByGroupIdAndMemberId(groupId, memberId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.NOT_GROUP_MEMBER));

        // 초대 토큰 생성 (UUID로 생성, 우선 만료일 7일로 설정함)
        GroupInviteToken inviteToken = GroupInviteToken.builder()
                .group(group)
                .inviter(groupMember.getMember())
                .build();

        inviteTokenRepository.save(inviteToken);

        return GroupInviteResponseDto.from(inviteToken, baseUrl);
    }

    // 그룹의 활성화된 초대 링크 목록 조회
    public List<GroupInviteResponseDto> getActiveInviteTokens(Long groupId, Long memberId) {
        if (!groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId)) {
            throw new GroupException(GroupErrorCode.NOT_GROUP_MEMBER);
        }

        List<GroupInviteToken> tokens = inviteTokenRepository
                .findActiveTokensByGroupId(groupId, LocalDateTime.now());

        return tokens.stream()
                .map(token -> GroupInviteResponseDto.from(token, baseUrl))
                .toList();
    }

    // 초대 링크 정보 미리보기
    public InvitePreviewResponseDto getInvitePreview(String inviteToken) {
        GroupInviteToken token = inviteTokenRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new GroupException(GroupErrorCode.INVITE_NOT_FOUND));

        Group group = token.getGroup();

        return InvitePreviewResponseDto.of(
                group,
                token.isExpired(),
                token.isUsable()
        );
    }

    // 초대 링크로 그룹 가입
    @Transactional
    public void joinByInviteToken(String inviteToken, Long memberId) {
        GroupInviteToken token = inviteTokenRepository.findByInviteToken(inviteToken)
                .orElseThrow(() -> new GroupException(GroupErrorCode.INVITE_NOT_FOUND));

        if (!token.isUsable()) {
            throw new GroupException(GroupErrorCode.INVITE_EXPIRED_OR_INVALID);
        }

        Group group = token.getGroup();

        // 이미 그룹 멤버인지 확인
        if (groupMemberRepository.existsByGroupIdAndMemberId(group.getId(), memberId)) {
            throw new GroupException(GroupErrorCode.ALREADY_GROUP_MEMBER);
        }

        // 그룹이 꽉 찼는지 확인
        if (group.isFull()) {
            throw new GroupException(GroupErrorCode.GROUP_FULL);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.MEMBER_NOT_FOUND));

        // 그룹 멤버 추가
        GroupMember groupMember = GroupMember.builder()
                .group(group)
                .member(member)
                .role(GroupMember.GroupRole.MEMBER)
                .build();

        groupMemberRepository.save(groupMember);

        group.incrementMemberCount();
    }

    // 초대 링크 삭제(soft delete)
    @Transactional
    public void deleteInviteToken(Long groupId, Long inviteId, Long memberId) {
        if (!groupMemberRepository.existsByGroupIdAndMemberId(groupId, memberId)) {
            throw new GroupException(GroupErrorCode.NOT_GROUP_MEMBER);
        }

        GroupInviteToken token = inviteTokenRepository.findById(inviteId)
                .orElseThrow(() -> new GroupException(GroupErrorCode.INVITE_NOT_FOUND));

        // 해당 그룹의 토큰인지 확인
        if (!token.getGroup().getId().equals(groupId)) {
            throw new GroupException(GroupErrorCode.INVALID_INVITE);
        }

        if (!token.getInviter().getId().equals(memberId)) {
            throw new GroupException(GroupErrorCode.NOT_INVITE_CREATOR);
        }

        token.softDelete();
    }
}