package com.example.muaring.domain.group.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupCategoryType {

    POP("pop", "팝"),
    K_POP("k_pop", "케이팝"),
    HIP_HOP_RAP("hip_hop_rap", "힙합 / 랩"),
    RNB_SOUL("rnb_soul", "R&B / 소울"),
    ROCK("rock", "록 / 얼터너티브"),
    INDIE("indie", "인디 / 어쿠스틱"),
    ELECTRONIC("electronic", "일렉트로닉 / EDM"),
    BALLAD("ballad", "발라드"),
    JAZZ_BLUES("jazz_blues", "재즈 / 블루스"),
    CLASSICAL("classical", "클래식 / 오페라"),
    SOUNDTRACK("soundtrack", "OST / 영화·드라마 음악"),
    METAL("metal", "메탈 / 하드록"),
    LATIN("latin", "라틴 / 월드뮤직"),
    HIPHOP_KOREAN("hiphop_korean", "국힙"),
    OTHERS("others", "기타");

    private final String name;
    private final String displayName;
}
