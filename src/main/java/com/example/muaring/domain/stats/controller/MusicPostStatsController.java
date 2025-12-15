package com.example.muaring.domain.stats.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.stats.dto.DailyTopMusicResponse;
import com.example.muaring.domain.stats.service.MusicPostStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MusicPostStatsController {

    private final MusicPostStatsService musicPostStatsService;

    /**
     * 어제의 인기곡 TOP 3
     */
    @GetMapping("/stats/musics/top3/yesterday")
    public ApiResponse<DailyTopMusicResponse> getYesterdayTop3() {
        return ApiResponse.ok(
                musicPostStatsService.getYesterdayTop3(),
                "어제 가장 많이 공유된 음악 TOP 3입니다."
        );
    }

    /**
     * 지난 7일간 인기곡 TOP 3
     */
    @GetMapping("/stats/musics/top3/last7days")
    public ApiResponse<DailyTopMusicResponse> getLast7DaysTop3() {
        return ApiResponse.ok(
                musicPostStatsService.getLast7DaysTop3(),
                "지난 7일 동안 가장 많이 공유된 음악 TOP 3입니다."
        );
    }
}
