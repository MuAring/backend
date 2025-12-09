package com.example.muaring.domain.recommendation.repository;

import com.example.muaring.domain.recommendation.entity.MemberGroupSimilarityCache;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberGroupSimilarityCacheRepository
        extends JpaRepository<MemberGroupSimilarityCache, Long> {

    Optional<MemberGroupSimilarityCache> findByMemberIdAndGroupId(Long memberId, Long groupId);

    List<MemberGroupSimilarityCache> findByMemberIdOrderByTotalScoreDesc(Long memberId, Pageable pageable);

    void deleteAllByMemberId(Long memberId);
}
