package com.example.muaring.domain.member.entity;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.file.entity.Image;
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

    @OneToMany(mappedBy = "member", orphanRemoval = true)
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

    public void createProfile(String nickname, Image image) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        if (nickname.length() > 10) {
            throw new IllegalArgumentException("닉네임은 10자를 초과할 수 없습니다.");
        }
        this.nickname = nickname;
        this.profileImage = image;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImage(Image image) {
        this.profileImage = image;
    }

    public void updateIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void updateDiscoveryEnabled(Boolean discoveryEnabled) {
        this.discoveryEnabled = discoveryEnabled;
    }
}
