package com.example.muaring.domain.group.entity;

import com.example.muaring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "join_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_invite_token_id", nullable = false)
    private GroupInviteToken groupInviteToken;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private JoinRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // status enum으로 관리, 필요할 시 파일 분리할 것
    public enum JoinRequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
