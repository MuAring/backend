package com.example.muaring.domain.recommendation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "member_group_similarity_cache",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_member_group",
                        columnNames = {"member_id", "group_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberGroupSimilarityCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "similarity_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

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

    // ======= 생성 팩토리 =======
    public static MemberGroupSimilarityCache of(
            Long memberId,
            Long groupId,
            Double totalScore,
            Double genreScore,
            Double audioScore,
            Double artistScore,
            Double rarityScore,
            LocalDateTime calculatedAt
    ) {
        MemberGroupSimilarityCache cache = new MemberGroupSimilarityCache();
        cache.memberId = memberId;
        cache.groupId = groupId;
        cache.totalScore = totalScore;
        cache.genreScore = genreScore;
        cache.audioScore = audioScore;
        cache.artistScore = artistScore;
        cache.rarityScore = rarityScore;
        cache.calculatedAt = calculatedAt;
        return cache;
    }

    // ======= update 메서드 =======
    public void updateScores(
            Double totalScore,
            Double genreScore,
            Double audioScore,
            Double artistScore,
            Double rarityScore,
            LocalDateTime calculatedAt
    ) {
        this.totalScore = totalScore;
        this.genreScore = genreScore;
        this.audioScore = audioScore;
        this.artistScore = artistScore;
        this.rarityScore = rarityScore;
        this.calculatedAt = calculatedAt;
    }
}
