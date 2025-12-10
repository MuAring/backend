package com.example.muaring.domain.recommendation.repository;

import com.example.muaring.domain.recommendation.entity.GroupRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRecommendationRepository extends JpaRepository<GroupRecommendation, Long> {

    Optional<GroupRecommendation> findByMember_IdAndRecommendedGroup_Id(Long memberId, Long groupId);
}