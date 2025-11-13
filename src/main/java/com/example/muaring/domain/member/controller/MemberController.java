package com.example.muaring.domain.member.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.common.security.SecurityUtil;
import com.example.muaring.domain.member.dto.request.MemberProfileCreateRequestDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileCreateResponseDTO;
import com.example.muaring.domain.member.dto.response.NicknameCheckResponseDTO;
import com.example.muaring.domain.member.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
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
}