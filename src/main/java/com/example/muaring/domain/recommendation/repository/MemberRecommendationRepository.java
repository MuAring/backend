package com.example.muaring.domain.recommendation.repository;

import com.example.muaring.domain.recommendation.entity.MemberRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRecommendationRepository extends JpaRepository<MemberRecommendation, Long> {

    Optional<MemberRecommendation> findByMember_IdAndRecommendedMember_Id(Long memberId, Long recommendedMemberId);
}