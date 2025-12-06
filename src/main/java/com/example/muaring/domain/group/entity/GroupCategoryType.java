package com.example.muaring.domain.group.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupCategoryType {

    POP("pop", "팝"),
    KPOP("kpop", "케이팝"),
    JPOP("jpop", "J-POP"),
    HIPHOP("hiphop", "힙합"),
    RAP("rap", "랩"),
    RNB("rnb", "R&B"),
    SOUL("soul", "소울"),
    ROCK("rock", "록"),
    ALT("alt", "얼터너티브"),
    INDIE("indie", "인디"),
    ACOUSTIC("acoustic", "어쿠스틱"),
    EDM("edm", "EDM"),
    DNB("dnb", "드럼앤베이스"),
    TRAP("trap", "트랩"),
    JAZZ("jazz", "재즈"),
    BLUES("blues", "블루스"),
    CLASSIC("classic", "클래식"),
    OST("ost", "OST"),
    METAL("metal", "메탈"),
    HARDROCK("hardrock", "하드록"),
    LATIN("latin", "라틴"),
    REGGAE("reggae", "레게"),
    FOLK("folk", "포크"),
    CITYPOP("citypop", "시티팝"),
    AMBIENT("ambient", "앰비언트"),
    CHILL("chill", "칠"),
    OTHERS("others", "기타");

    private final String name;
    private final String displayName;

    public static GroupCategoryType fromName(String name) {
        for (GroupCategoryType type : GroupCategoryType.values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }

        return OTHERS;
    }
}
