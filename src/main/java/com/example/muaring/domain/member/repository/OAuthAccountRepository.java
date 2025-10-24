package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.auth.entity.AuthProvider;
import com.example.muaring.domain.member.entity.OAuthAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    Optional<OAuthAccount> findByAuthProviderAndAuthProviderId(AuthProvider authProvider, String authProviderId);
}