package com.example.muaring.domain.member.controller;


import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.member.dto.response.FollowResponseDTO;
import com.example.muaring.domain.member.service.FollowService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse<FollowResponseDTO>> sendFollowRequest(@RequestParam Long followeeId) {
        FollowResponseDTO response = followService.sendFollowRequest(followeeId);
        return ResponseEntity.ok(ApiResponse.ok(response, "팔로우 요청을 보냈습니다."));
    }

    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<FollowResponseDTO>> approveFollowRequest(@RequestParam Long requestId) {
        FollowResponseDTO response = followService.approveFollowRequest(requestId);
        return ResponseEntity.ok(ApiResponse.ok(response, "팔로우 요청을 승인하였습니다."));
    }
    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<FollowResponseDTO>> rejectFollowRequest(@RequestParam Long requestId) {
        FollowResponseDTO response = followService.rejectFollowRequest(requestId);
        return ResponseEntity.ok(ApiResponse.ok(response, "팔로우 요청을 거절하였습니다."));
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<ApiResponse<Void>> deleteFollowRequest(@RequestParam Long followeeId) {
        followService.unfollow(followeeId);
        return ResponseEntity.ok(ApiResponse.ok(null, "팔로우가 해제되었습니다."));
    }
}

