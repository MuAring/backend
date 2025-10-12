package com.example.muaring.domain.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(length = 10, nullable = false)
    private String nickname;

    @Column(name = "auth_provider", length = 20, nullable = false)
    private String authProvider;

    @Column(name = "auth_provider_id", length = 100, nullable = false)
    private String authProviderId;

    @Column(length = 20, nullable = false)
    private Status status;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "discovery_enabled", nullable = false)
    private Boolean discoveryEnabled;

    @Column(name = "noti_enabled", nullable = false)
    private Boolean notiEnabled;

    @Column(name = "noti_time", nullable = false)
    private LocalTime notiTime;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public enum Status {
        ACTIVE,
        DELETED
    }
}
