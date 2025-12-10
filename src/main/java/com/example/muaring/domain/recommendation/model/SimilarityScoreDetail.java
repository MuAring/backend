package com.example.muaring.domain.recommendation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SimilarityScoreDetail {

    private final double totalScore;
    private final double genreScore;   // 지금은 0, 나중에 장르 붙이면 사용
    private final double audioScore;
    private final double artistScore;
    private final double rarityScore;
}
