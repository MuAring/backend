package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.auth.entity.AuthProvider;
import com.example.muaring.domain.member.entity.OAuthAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    @EntityGraph(attributePaths = "member") // 한 번의 쿼리로 OAuthAccount와 Member를 모두 가져옴
    Optional<OAuthAccount> findByAuthProviderAndAuthProviderId(AuthProvider authProvider, String authProviderId);
}