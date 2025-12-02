package com.example.muaring.domain.nearby.controller;

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
    public ResponseEntity<List<TodayNearbyMusicDTO>> getTodayNearbyMusic(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "1") double radiusKm
    ) {

        List<TodayNearbyMusicDTO> result =
                nearbyService.getTodayMusicOfNearbyUsers(lat, lng, radiusKm);

        return ResponseEntity.ok(result);
    }
}
