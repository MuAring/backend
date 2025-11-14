package com.example.muaring.domain.group.repository;

import com.example.muaring.domain.group.entity.GroupInviteToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupInviteTokenRepository extends JpaRepository<GroupInviteToken, Long> {

    Optional<GroupInviteToken> findByInviteToken(String inviteToken);

    // 그룹의 활성화된 초대 링크 목록 (삭제되지 않고 만료되지 않음)
    @Query("SELECT git FROM GroupInviteToken git " +
            "WHERE git.group.id = :groupId " +
            "AND git.isDeleted = false " +
            "AND git.expiresAt > :now " +
            "ORDER BY git.createdAt DESC")
    List<GroupInviteToken> findActiveTokensByGroupId(
            @Param("groupId") Long groupId,
            @Param("now") LocalDateTime now
    );

    // 특정 멤버가 생성한 초대 링크 목록
    @Query("SELECT git FROM GroupInviteToken git " +
            "WHERE git.createdBy.id = :memberId " +
            "AND git.isDeleted = false " +
            "ORDER BY git.createdAt DESC")
    List<GroupInviteToken> findByCreatedById(@Param("memberId") Long memberId);
}