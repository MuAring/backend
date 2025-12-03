package com.example.muaring.domain.nearby.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.nearby.dto.LocationRequestDTO;
import com.example.muaring.domain.nearby.dto.LocationResponse;
import com.example.muaring.domain.nearby.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(@RequestBody LocationRequestDTO req) {

        locationService.updateLocation(req.getLatitude(), req.getLongitude());

        return ResponseEntity.ok(ApiResponse.ok("가용자 위치를 업데이트하였습니다."));

    }
}
