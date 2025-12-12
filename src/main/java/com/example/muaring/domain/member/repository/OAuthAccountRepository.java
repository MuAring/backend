package com.example.muaring.domain.member.repository;

import com.example.muaring.domain.auth.entity.AuthProvider;
import com.example.muaring.domain.member.entity.OAuthAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

    @EntityGraph(attributePaths = "member") // 한 번의 쿼리로 OAuthAccount와 Member를 모두 가져옴
    Optional<OAuthAccount> findByAuthProviderAndAuthProviderId(AuthProvider authProvider, String authProviderId);
    Optional<OAuthAccount> findByMemberIdAndAuthProvider(Long memberId, AuthProvider authProvider);
}