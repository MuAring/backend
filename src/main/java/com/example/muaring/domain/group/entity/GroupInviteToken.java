package com.example.muaring.domain.group.entity;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "group_invite_token",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"invite_token"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupInviteToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_invite_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Member createdBy;

    @Column(name = "invite_token", length = 36, nullable = false, unique = true)
    private String inviteToken;

    @Column(name="expires_at", nullable = false)
    private LocalDateTime expiresAt;


    @Builder
    public GroupInviteToken(Group group, Member createdBy) {
        this.group = group;
        this.createdBy = createdBy;
        this.inviteToken = UUID.randomUUID().toString();
        this.expiresAt = LocalDateTime.now().plusDays(7);  // 기본 7일
    }

    // 만료 여부 확인
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // 사용 가능 여부 확인
    public boolean isUsable() {
        return !getIsDeleted() && !isExpired();
    }

    public void softDelete() {
        this.markDeleted();
    }
}


