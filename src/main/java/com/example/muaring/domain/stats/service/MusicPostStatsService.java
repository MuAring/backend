package com.example.muaring.domain.stats.service;
import com.example.muaring.domain.stats.dto.DailyTopMusicResponse;
import com.example.muaring.domain.stats.repository.MusicPostStatsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicPostStatsService {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private final MusicPostStatsRepository statsRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public DailyTopMusicResponse getYesterdayTop3() {
        LocalDate today = LocalDate.now(KST);
        LocalDate yesterday = today.minusDays(1);

        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = today.atStartOfDay();

        String cacheKey = "stats:musicpost:top3:yesterday:" + yesterday.format(DATE);

        // 어제는 하루 동안 고정값 -> TTL 30h 추천
        return getTop3WithCache("yesterday", start, end, cacheKey, 30);
    }

    public DailyTopMusicResponse getLast7DaysTop3() {
        LocalDate today = LocalDate.now(KST);

        // 지난 7일: 어제 포함 7일 범위 (오늘은 미포함)
        // [today-7 00:00, today 00:00)
        LocalDate startDay = today.minusDays(7);

        LocalDateTime start = startDay.atStartOfDay();
        LocalDateTime end = today.atStartOfDay();

        String cacheKey = "stats:musicpost:top3:last7days:" + startDay.format(DATE) + ":" + today.format(DATE);

        // 지난 7일도 자주 안 바뀜 -> TTL 24h
        return getTop3WithCache("last7days", start, end, cacheKey, 24);
    }

    private DailyTopMusicResponse getTop3WithCache(
            String period,
            LocalDateTime start,
            LocalDateTime end,
            String cacheKey,
            long ttlHours
    ) {
        // 1) Redis 먼저
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null && !cached.isBlank()) {
            try {
                return objectMapper.readValue(cached, DailyTopMusicResponse.class);
            } catch (Exception ignored) {
                // 캐시 깨졌으면 DB로 재생성
            }
        }

        // 2) DB 집계
        List<MusicPostStatsRepository.TopMusicRow> rows = statsRepository.findTop3ByPeriod(start, end);

        List<DailyTopMusicResponse.Item> items = rows.stream()
                .map(r -> new DailyTopMusicResponse.Item(
                        r.getTitle(),
                        r.getArtistName(),
                        r.getAlbumImgUrl(),
                        r.getShareCount() == null ? 0L : r.getShareCount()
                ))
                .collect(Collectors.toList());

        // 표기용 날짜(yyyy-MM-dd)
        String startDate = start.toLocalDate().format(DATE);
        String endDate = end.toLocalDate().format(DATE);

        DailyTopMusicResponse response = new DailyTopMusicResponse(period, startDate, endDate, items);

        // 3) Redis 저장
        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, json, ttlHours, TimeUnit.HOURS);
        } catch (Exception ignored) { }

        return response;
    }
}