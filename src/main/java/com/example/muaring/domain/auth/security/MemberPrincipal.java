package com.example.muaring.domain.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

// ✨ JWT에서 꺼낸 정보만 최소로 보관하는 객체 (인증 객체)
@Getter
@AllArgsConstructor
public class MemberPrincipal {

    private final Long memberId;
    private final String email;
}