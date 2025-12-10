package com.example.muaring.domain.member.entity;

import com.example.muaring.domain.auth.entity.AuthProvider;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "oauth_account",
        uniqueConstraints = @UniqueConstraint(name = "uk_provider_providerId", columnNames = {"auth_provider", "auth_provider_id"})
)
public class OAuthAccount {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "o_auth_account_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider authProvider;

    @Column(name = "auth_provider_id")
    private String authProviderId;

    @Column(name = "spotify_refresh_token")
    private String spotifyRefreshToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public static OAuthAccount createOAuthAccount(AuthProvider authProvider, String providerId, Member member) {
        OAuthAccount oauthAccount = new OAuthAccount();
        oauthAccount.authProvider = authProvider;
        oauthAccount.authProviderId = providerId;
        oauthAccount.member = member;
        return oauthAccount;
    }

    public void updateSpotifyRefreshToken(String spotifyRefreshToken) {
        this.spotifyRefreshToken = spotifyRefreshToken;
    }
}