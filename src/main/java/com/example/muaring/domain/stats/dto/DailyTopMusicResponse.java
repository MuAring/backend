package com.example.muaring.domain.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DailyTopMusicResponse {

    private String period; // e.g. "yesterday", "last7days"
    private String startDate; // yyyy-MM-dd
    private String endDate;   // yyyy-MM-dd (exclusive end or display end)
    private List<Item> musics;

    @Getter
    @AllArgsConstructor
    public static class Item {
        private String title;
        private String artistName;
        private String albumImgUrl;
        private long shareCount; // 필요 없으면 빼도 됨
    }
}