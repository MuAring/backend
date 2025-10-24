package com.example.muaring.domain.member.entity;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.file.Image;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 10)
    private String nickname;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private Image profileImage;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name="oauth_accounts")
    private List<OAuthAccount> oauthAccounts = new ArrayList<>();

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;

    @Column(name = "discovery_enabled", nullable = false)
    private Boolean discoveryEnabled = false;

    @Column(name = "noti_enabled", nullable = false)
    private Boolean notiEnabled = true;

    @Column(name = "noti_time", nullable = false)
    private LocalTime notiTime = LocalTime.of(10, 0, 0);

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static Member CreateOAuthMember(String email) {
        Member member = new Member();
        member.email = email;
        return member;
    }
}
