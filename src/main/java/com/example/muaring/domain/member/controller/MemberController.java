package com.example.muaring.domain.member.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.member.dto.request.MemberProfileCreateRequestDTO;
import com.example.muaring.domain.member.dto.response.MemberProfileResponseDTO;
import com.example.muaring.domain.member.dto.response.NicknameCheckResponseDTO;
import com.example.muaring.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 닉네임 중복 체크
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<NicknameCheckResponseDTO>> checkNicknameDuplicated(@RequestParam("nickname") String nickname) {
        NicknameCheckResponseDTO response = memberService.checkNicknameDuplicated(nickname);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(response, "닉네임 중복체크가 완료되었습니다."));
    }

    // 프로필 등록 (최초)
    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<MemberProfileResponseDTO>> updateProfile(
            @Valid @RequestBody MemberProfileCreateRequestDTO requestDTO
    ) {
        MemberProfileResponseDTO responseDTO = memberService.registerProfile(requestDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(responseDTO, "프로필이 생성되었습니다."));
    }
}