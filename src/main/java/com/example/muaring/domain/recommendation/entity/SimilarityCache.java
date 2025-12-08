package com.example.muaring.domain.recommendation.entity;

import com.example.muaring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "similarity_cache",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id_a", "member_id_b"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimilarityCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "similarity_id")
    private Long id;

    // 사용자 A (ID 작은 쪽)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id_a", nullable = false)
    private Member memberA;

    // 사용자 B (ID 큰 쪽)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id_b", nullable = false)
    private Member memberB;

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

    public static SimilarityCache create(Member memberA,
                                         Member memberB,
                                         Double totalScore,
                                         Double genreScore,
                                         Double audioScore,
                                         Double artistScore,
                                         Double rarityScore,
                                         LocalDateTime calculatedAt) {
        SimilarityCache cache = new SimilarityCache();
        cache.memberA = memberA;
        cache.memberB = memberB;
        cache.totalScore = totalScore;
        cache.genreScore = genreScore;
        cache.audioScore = audioScore;
        cache.artistScore = artistScore;
        cache.rarityScore = rarityScore;
        cache.calculatedAt = calculatedAt;
        return cache;
    }

    public void updateScores(Double totalScore,
                             Double genreScore,
                             Double audioScore,
                             Double artistScore,
                             Double rarityScore,
                             LocalDateTime calculatedAt) {
        this.totalScore = totalScore;
        this.genreScore = genreScore;
        this.audioScore = audioScore;
        this.artistScore = artistScore;
        this.rarityScore = rarityScore;
        this.calculatedAt = calculatedAt;
    }

}
