package com.example.muaring.domain.group;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "group_member",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"group_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_member_id")
    private Long groupMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private GroupRole role;

    // 역할 enum으로 관리, 필요할 시 파일 분리할 것
    public enum GroupRole {
        ADMIN,
        MEMBER
    }
}
