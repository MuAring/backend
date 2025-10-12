package com.example.muaring.domain.notification;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private NotificationType type;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(length = 255)
    private String content;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    // type enum으로 관리, 필요할 시 파일 분리할 것
    public enum NotificationType {
        TODAY_MUSIC,
        LIKE,
        COMMENT,
        FOLLOW_APPROVED,
        NEW_FOLLOWER,
        INTERESTED_USER
    }
}
