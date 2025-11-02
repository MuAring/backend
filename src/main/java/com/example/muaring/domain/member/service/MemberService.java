package com.example.muaring.domain.member.service;

import com.example.muaring.domain.member.dto.response.NicknameCheckResponseDTO;
import com.example.muaring.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public NicknameCheckResponseDTO checkNicknameDuplicated(String nickname) {
        boolean isDuplicated = memberRepository.existsByNicknameAndIsDeletedFalse(nickname);
        return NicknameCheckResponseDTO.of(nickname, isDuplicated);
    }
}