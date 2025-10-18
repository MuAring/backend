package com.example.muaring.domain.member;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "member_recommendation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "recommended_member_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long id;

    // 추천을 받는 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 추천된 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_member_id", nullable = false)
    private Member recommendedMember;

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
