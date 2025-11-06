package com.example.muaring.domain.member.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.auth.exception.AuthErrorCode;
import com.example.muaring.domain.group.dto.MyGroupListResponseDto;
import com.example.muaring.domain.group.exception.GroupErrorCode;
import com.example.muaring.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final GroupService groupService;

    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<MyGroupListResponseDto>> getMyGroups() {
        Long memberId = SecurityUtil.getMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.fail(AuthErrorCode.UNAUTHORIZED_USER,null));
        }

        ApiResponse<MyGroupListResponseDto> body = ApiResponse.ok(groupService.getMyGroups(memberId));
        return ResponseEntity.ok(body);
    }
}
