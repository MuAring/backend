package com.example.muaring.domain.recommendation.repository;

import com.example.muaring.domain.recommendation.entity.SimilarityCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SimilarityCacheRepository extends JpaRepository<SimilarityCache, Long> {

    Optional<SimilarityCache> findByMemberA_IdAndMemberB_Id(Long memberAId, Long memberBId);

    @Modifying
    @Query("""
        delete from SimilarityCache s
        where s.memberA.id = :memberId
           or s.memberB.id = :memberId
    """)
    void deleteAllByMemberId(Long memberId);

    List<SimilarityCache> findTop100ByMemberA_IdOrMemberB_IdOrderByTotalScoreDesc(Long memberIdA, Long memberIdB);

}