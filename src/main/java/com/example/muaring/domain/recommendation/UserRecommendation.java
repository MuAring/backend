package com.example.muaring.domain.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_recommendation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "recommended_user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long recommendationId;

    // 추천을 받는 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 추천된 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_user_id", nullable = false)
    private User recommendedUser;

    @Column(name = "similarity_score", nullable = false)
    private Double similarityScore;

    @Column(name = "recommendation_rank", nullable = false)
    private Integer recommendationRank;

    @Column(name = "shown_at", nullable = false)
    private LocalDateTime shownAt;

    @Column(name = "clicked", nullable = false)
    private Boolean clicked = false;

    @Column(name = "followed", nullable = false)
    private Boolean followed = false;

    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;

    @Column(name = "followed_at")
    private LocalDateTime followedAt;
}
