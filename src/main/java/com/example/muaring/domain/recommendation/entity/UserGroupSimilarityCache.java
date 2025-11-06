package com.example.muaring.domain.recommendation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_group_similarity_cache",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_group", columnNames = {"member_id", "group_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserGroupSimilarityCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "similarity_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "group_id", nullable = false)
    private Long groupId;

    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    @Column(name = "genre_score", nullable = false)
    private Double genreScore;

    @Column(name = "audio_score", nullable = false)
    private Double audioScore;

    @Column(name = "artist_score", nullable = false)
    private Double artistScore;

    @Column(name = "rarity_score", nullable = false)
    private Double rarityScore;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;
}
