package com.example.muaring.domain.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

// ✨ JWT에서 꺼낸 정보만 최소로 보관하는 객체 (인증 객체)
public record MemberPrincipal(
        Long memberId,
        String email
) { }