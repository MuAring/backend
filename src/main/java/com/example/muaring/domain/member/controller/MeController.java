package com.example.muaring.domain.member.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.auth.exception.AuthErrorCode;
import com.example.muaring.domain.group.dto.MyGroupListResponseDto;
import com.example.muaring.domain.group.service.GroupService;
import com.example.muaring.domain.member.dto.request.MemberProfileUpdateRequestDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileUpdateResponseDTO;
import com.example.muaring.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원 본인 정보 API", description = "회원 본인 정보 관련 API입니다.")
@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class MeController {

    private final MemberService memberService;
    private final GroupService groupService;

    @Operation(summary = "내 그룹 조회", description = "내가 참여한 그룹 조회 로직입니다.")
    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<MyGroupListResponseDto>> getMyGroups() {
        Long memberId = SecurityUtil.getMemberId();
        if (memberId == null) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.fail(AuthErrorCode.UNAUTHORIZED_MEMBER,null));
        }

        ApiResponse<MyGroupListResponseDto> body = ApiResponse.ok(groupService.getMyGroups(memberId));
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "내 프로필 수정", description = "내 프로필 수정 로직입니다.")
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<MemberProfileUpdateResponseDTO>> updateProfile(
            @Valid @RequestBody MemberProfileUpdateRequestDTO requestDTO
    ) {
        MemberProfileUpdateResponseDTO responseDTO = memberService.updateProfile(requestDTO);
        return ResponseEntity.ok(ApiResponse.ok(responseDTO, "프로필이 수정되었습니다."));
    }
}
