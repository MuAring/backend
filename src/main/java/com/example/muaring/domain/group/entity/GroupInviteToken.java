package com.example.muaring.domain.group.entity;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "invite_token", length = 36, nullable = false, unique = true)
    private String inviteToken;
}


