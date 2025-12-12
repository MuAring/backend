package com.example.muaring.domain.member.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.member.dto.request.MemberProfileCreateRequestDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileCreateResponseDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileReadResponseDTO;
import com.example.muaring.domain.member.dto.response.MemberSearchItemDto;
import com.example.muaring.domain.member.dto.response.NicknameCheckResponseDTO;
import com.example.muaring.domain.member.service.MemberService;
import com.example.muaring.domain.music.dto.MusicHistoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<NicknameCheckResponseDTO>> checkNicknameDuplicated(
            @RequestParam("nickname")
            @NotBlank(message = "낙네임 입력은 필수입니다.")
            String nickname) {
        NicknameCheckResponseDTO response = memberService.checkNicknameDuplicated(nickname);
        return ResponseEntity.ok(
                ApiResponse.ok(response, "닉네임 중복체크가 완료되었습니다."));
    }

    // 프로필 등록 (최초)
    @PostMapping
    public ResponseEntity<ApiResponse<MemberProfileCreateResponseDTO>> updateProfile(
            @Valid @RequestBody MemberProfileCreateRequestDTO requestDTO
    ) {
        Long memberId = SecurityUtil.getMemberId();
        MemberProfileCreateResponseDTO responseDTO = memberService.registerProfile(memberId, requestDTO);
        return ResponseEntity.ok(
                ApiResponse.ok(responseDTO, "프로필이 생성되었습니다."));
    }

    @Operation(summary = "프로필 조회", description = "프로필 조회 로직입니다.")
    @GetMapping("/{memberId}/profile")
    public ResponseEntity<ApiResponse<MemberProfileReadResponseDTO>> getProfile(@PathVariable Long memberId) {
        Long loginMemberId = SecurityUtil.getMemberId();
        MemberProfileReadResponseDTO responseDTO = memberService.getProfile(memberId, loginMemberId);
        return ResponseEntity.ok(
                ApiResponse.ok(responseDTO, "프로필이 조회되었습니다."));
    }

    // [GET] /members/search?name=닉네임일부
    @Operation(summary = "닉네임 검색", description = "닉네임으로 멤버를 조회합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<MemberSearchItemDto>>> searchMembers(
            @RequestParam String name,
            Pageable pageable
    ) {
        Page<MemberSearchItemDto> result = memberService.searchMembers(name, pageable);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{memberId}/history")
    public ResponseEntity<ApiResponse<Page<MusicHistoryDTO>>> getMusicHistoryByMember(
            @PathVariable Long memberId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @PageableDefault(page = 0, size = 20) Pageable pageable
    ) {
        Page<MusicHistoryDTO> history = memberService.getMusicHistoryByMember(memberId, year, month, pageable);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(history, "회원의 음악 히스토리 조회가 완료되었습니다."));
    }

}