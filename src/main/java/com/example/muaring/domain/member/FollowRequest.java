package com.example.muaring.domain.member;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "follow_request",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FollowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_request_id")
    private Long id;

    // 팔로우를 건 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower;

    // 팔로우 요청을 받은 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id", nullable = false)
    private Member followee;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private FollowRequestStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // status enum으로 관리, 필요할 시 파일 분리할 것
    public enum FollowRequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
