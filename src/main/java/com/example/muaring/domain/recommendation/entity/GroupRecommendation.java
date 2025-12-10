package com.example.muaring.domain.recommendation.entity;

import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "group_recommendation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "recommended_group_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommendation_id")
    private Long id;

    // 추천을 받는 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 추천된 그룹
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommended_group_id", nullable = false)
    private Group recommendedGroup;

    @Column(name = "similarity_score", nullable = false)
    private Double similarityScore;

    @Column(name = "recommendation_rank", nullable = false)
    private Integer recommendationRank;

    @Column(name = "shown_at", nullable = false)
    private LocalDateTime shownAt;

    @Column(name = "clicked", nullable = false)
    private Boolean clicked = false;

    @Column(name = "joined", nullable = false)
    private Boolean joined = false;

    @Column(name = "clicked_at")
    private LocalDateTime clickedAt;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    // === 생성 편의 메서드 ===
    public static GroupRecommendation create(Member member,
                                             Group group,
                                             Double similarityScore,
                                             Integer recommendationRank,
                                             LocalDateTime shownAt) {
        GroupRecommendation rec = new GroupRecommendation();
        rec.member = member;
        rec.recommendedGroup = group;
        rec.similarityScore = similarityScore;
        rec.recommendationRank = recommendationRank;
        rec.shownAt = shownAt;
        rec.clicked = false;
        rec.joined = false;
        return rec;
    }

    // === 상태 업데이트 ===
    public void updateOnShown(Double similarityScore,
                              Integer recommendationRank,
                              LocalDateTime shownAt) {
        this.similarityScore = similarityScore;
        this.recommendationRank = recommendationRank;
        this.shownAt = shownAt;
    }

    public void markClicked(LocalDateTime clickedAt) {
        this.clicked = true;
        this.clickedAt = clickedAt;
    }

    public void markJoined(LocalDateTime joinedAt) {
        this.joined = true;
        this.joinedAt = joinedAt;
    }
}
