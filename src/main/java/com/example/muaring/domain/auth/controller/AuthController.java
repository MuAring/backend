package com.example.muaring.domain.auth.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.auth.dto.request.KakaoLoginRequest;
import com.example.muaring.domain.auth.dto.response.LoginResponseDTO;
import com.example.muaring.domain.auth.entity.AuthProvider;
import com.example.muaring.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ⚪ 카카오 로그인 (SDK)
    @PostMapping("/kakao")
    @Operation(summary = "카카오 소셜로그인 인가 코드 수신", description = "소셜 서버로부터 요청되는 인가 코드 수신 로직입니다.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginWithKakao(
            @RequestBody KakaoLoginRequest request
    ) {
        LoginResponseDTO response = authService.kakaoLogin(request);
        System.out.println("3"+response.accessToken());
        System.out.println("3"+response.email());
        return ResponseEntity
                .ok(ApiResponse.ok(response, "카카오 로그인에 성공했습니다."));
    }

//    @Operation(summary = "카카오 소셜로그인", description = "카카오 소셜 로그인 로직입니다.")
//    @PostMapping
//    public ResponseEntity<ApiResponse<LoginResponseDTO>> kakaoLogin(
//            @RequestParam("code") String code
//    ) {
//        log.info("{} 로그인 요청 수신", provider.toUpperCase());
//        LoginResponseDTO response = authService.login(code, AuthProvider.valueOf(provider));
//        return ResponseEntity
//                .ok(ApiResponse.ok(response, provider + " 로그인에 성공했습니다."));
//    }

//    // ⚪ 카카오 서버로부터 리다이렉트 되는 엔드포인트 (테스트용)
//    @GetMapping("/login/{provider}")
//    @Operation(summary = "소셜로그인 인가 코드 수신", description = "소셜 서버로부터 요청되는 인가 코드 수신 로직입니다.")
//    public ResponseEntity<ApiResponse<String>> kakaoLoginRedirect(
//            @PathVariable("provider") String provider,
//            @RequestParam("code") String code
//    ) {
//        return ResponseEntity
//                .ok(ApiResponse.ok(provider.toUpperCase() + " 인가 코드가 생성되었습니다."));
//    }

    @Operation(summary = "스포티파이 소셜로그인", description = "소셜 로그인(KAKAO, SPOTIFY) 로직입니다.")
    @PostMapping("/spotify")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> spotifyLogin(
            @RequestParam("code") String code
    ) {
        log.info("로그인 요청 수신");
        LoginResponseDTO response = authService.login(code, AuthProvider.valueOf("SPOTIFY"));
        return ResponseEntity
                .ok(ApiResponse.ok(response, "스포티파이 로그인에 성공했습니다."));
    }
}