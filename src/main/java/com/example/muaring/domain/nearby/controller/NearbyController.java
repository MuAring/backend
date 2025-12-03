package com.example.muaring.domain.nearby.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.nearby.dto.TodayNearbyMusicDTO;
import com.example.muaring.domain.nearby.service.NearbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nearby")
public class NearbyController {

    private final NearbyService nearbyService;

    @GetMapping("/today-music")
    public ResponseEntity<ApiResponse<List<TodayNearbyMusicDTO>>> getTodayNearbyMusic(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1") double radiusKm
    ) {

        List<TodayNearbyMusicDTO> response =
                nearbyService.getTodayMusicOfNearbyUsers(lat, lng, radiusKm);

        return ResponseEntity.ok(ApiResponse.ok(response, "인근 사용자의 오늘의 음악을 조회하였습니다."));
    }
}
