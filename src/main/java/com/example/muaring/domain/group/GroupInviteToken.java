package com.example.muaring.domain.group;

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
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupInviteToken {

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

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private InviteStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // status enum으로 관리, 필요할 시 파일 분리할 것
    public enum InviteStatus {
        ACTIVE, EXPIRED
    }
}


