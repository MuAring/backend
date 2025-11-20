package com.example.muaring.domain.member.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.member.service.InterestedMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/interest")
public class InterestedMemberController {

    private final InterestedMemberService service;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Void>> addInterest(@RequestParam Long followeeId) {
        service.addInterest(followeeId);
        return ResponseEntity.ok(ApiResponse.ok(null, "관심 사용자로 추가되었습니다."));
    }

    @PostMapping("/remove")
    public ResponseEntity<ApiResponse<Void>> removeInterest(@RequestParam Long followeeId) {
        service.removeInterest(followeeId);
        return ResponseEntity.ok(ApiResponse.ok(null, "관심 사용자가 해제되었습니다."));
    }
}
