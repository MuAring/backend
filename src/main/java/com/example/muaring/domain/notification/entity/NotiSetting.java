package com.example.muaring.domain.notification.entity;

import com.example.muaring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "noti_setting",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "type"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotiSetting {

    @Id
    @Column(name = "noti_setting_id", length = 255)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private NotificationType type;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
