package com.example.muaring.domain.recommendation;

import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "similarity_cache",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id_a", "user_id_b"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimilarityCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "similarity_id")
    private Long similarityId;

    // 사용자 A (ID 작은 쪽)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_a", nullable = false)
    private User userA;

    // 사용자 B (ID 큰 쪽)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id_b", nullable = false)
    private User userB;

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
