package com.example.muaring.domain.member.entity;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.file.entity.Image;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 10, nullable = false)
    private String nickname;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;

    @Column(name = "auth_provider", length = 20, nullable = false)
    private String authProvider;

    @Column(name = "auth_provider_id", length = 100, nullable = false)
    private String authProviderId;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "discovery_enabled", nullable = false)
    private Boolean discoveryEnabled;

    @Column(name = "noti_enabled", nullable = false)
    private Boolean notiEnabled;

    @Column(name = "noti_time", nullable = false)
    private LocalTime notiTime;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
