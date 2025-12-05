package com.example.muaring.domain.member.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.group.dto.GroupListResponseDto;
import com.example.muaring.domain.group.service.GroupService;
import com.example.muaring.domain.member.dto.request.MemberProfileUpdateRequestDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileUpdateResponseDTO;
import com.example.muaring.domain.member.dto.response.MemberSettingsReadResponse;
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

    // [GET] /me/groups 내 그룹 전체 조회
    // [GET] /me/groups?name=그룹명 내 그룹 중 name 값을 갖고 있는 애들 조회
    @Operation(summary = "내 그룹 조회", description = "내가 참여한 그룹 조회 로직입니다.")
    @GetMapping("/groups")
    public ResponseEntity<ApiResponse<GroupListResponseDto>> getMyGroups(
            @RequestParam(required = false) String name
    ) {
        ApiResponse<GroupListResponseDto> body =
                ApiResponse.ok(groupService.getMyGroups(name));
        return ResponseEntity.ok(body);
    }


    @Operation(summary = "내 프로필 설정 정보 조회", description = "프로필 수정 화면에서 조회되는 프로필 설정 정보 조회 로직입니다.")
    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<MemberSettingsReadResponse>> getMemberSettings() {
        Long memberId = SecurityUtil.getMemberId();
        MemberSettingsReadResponse response = memberService.getMemberSettings(memberId);
        return ResponseEntity.ok(ApiResponse.ok(response, "내 프로필 설정 정보가 조회되었습니다."));
    }


    @Operation(summary = "내 프로필 수정", description = "내 프로필 수정 로직입니다.")
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<MemberProfileUpdateResponseDTO>> updateProfile(
            @Valid @RequestBody MemberProfileUpdateRequestDTO requestDTO
    ) {
        Long memberId = SecurityUtil.getMemberId();
        MemberProfileUpdateResponseDTO responseDTO = memberService.updateProfile(memberId, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok(responseDTO, "프로필이 수정되었습니다."));
    }
}
