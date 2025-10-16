package com.example.muaring.domain.group;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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
    private Long groupInviteTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "invite_token", length = 36, nullable = false, unique = true)
    private String inviteToken;
}


