package com.example.muaring.domain.nearby.service;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.member.entity.MemberLocation;
import com.example.muaring.domain.member.repository.MemberLocationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final MemberLocationRepository locationRepository;

    @Transactional
    public void updateLocation(double lat, double lng) {

        Long memberId = SecurityUtil.getMemberId();

        MemberLocation location = locationRepository.findById(memberId)
                .orElse(new MemberLocation(memberId, lat, lng, LocalDateTime.now()));

        location.update(lat, lng);
        locationRepository.save(location);
    }
}
