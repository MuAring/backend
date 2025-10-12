package com.example.muaring.domain.notification;

import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "noti_setting",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "type"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotiSetting {

    @Id
    @Column(name = "noti_setting_id", length = 255)
    private String notiSettingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private NotificationType type;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public enum NotificationType {
        TODAY_MUSIC,
        LIKE,
        COMMENT,
        FOLLOW_APPROVED,
        NEW_FOLLOWER,
        INTERESTED_USER
    }
}
