package com.example.muaring.domain.auth.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.auth.dto.request.SocialLoginRequestDTO;
import com.example.muaring.domain.auth.dto.response.LoginResponseDTO;
import com.example.muaring.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ⚪ 카카오 서버로부터 리다이렉트 되는 엔드포인트 (테스트용)
    @GetMapping("/login/kakao")
    @Operation(summary = "카카오 인가 코드 수신", description = "카카오 서버로부터 요청되는 인가 코드 수신 로직입니다.")
    public ResponseEntity<ApiResponse<String>> kakaoLoginRedirect(String code) {
        return ResponseEntity
                .ok(ApiResponse.ok("인가 코드가 생성되었습니다."));
    }

    @Operation(summary = "카카오 소셜 로그인", description = "카카오 소셜 로그인 로직입니다.")
    @PostMapping("/login/kakao")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> kakaoLogin(@RequestBody SocialLoginRequestDTO socialLoginRequestDTO) {
        log.info("카카오 로그인 요청 수신");
        LoginResponseDTO response = authService.login(socialLoginRequestDTO.code(), socialLoginRequestDTO.authProvider());
        return ResponseEntity
                .ok(ApiResponse.ok(response, socialLoginRequestDTO.authProvider() + "로그인에 성공했습니다."));
    }

    // ⚪ 스포티파이 서버로부터 리다이렉트 되는 엔드포인트 (테스트용)
    @GetMapping("/login/spotify")
    @Operation(summary = "스포티파이 인가 코드 수신", description = "스포티파이 서버로부터 요청되는 인가 코드 수신 로직입니다.")
    public ResponseEntity<ApiResponse<String>> spotifyLoginRedirect(String code) {
        return ResponseEntity
                .ok(ApiResponse.ok("인가 코드가 생성되었습니다."));

    }

    @Operation(summary = "스포티파이 소셜 로그인", description = "스포티파이 소셜 로그인 로직입니다.")
    @PostMapping("/login/spotify")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> spotifyLogin(@RequestBody SocialLoginRequestDTO socialLoginRequestDTO) {
        log.info("스포티파이 로그인 요청 수신");
        LoginResponseDTO response = authService.login(socialLoginRequestDTO.code(), socialLoginRequestDTO.authProvider());
        return ResponseEntity
                .ok(ApiResponse.ok(response, socialLoginRequestDTO.authProvider() + "로그인에 성공했습니다."));
    }
}