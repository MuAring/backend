package com.example.muaring.domain.auth.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.auth.dto.request.KakaoLoginRequest;
import com.example.muaring.domain.auth.dto.request.SpotifyLoginRequest;
import com.example.muaring.domain.auth.dto.response.AuthorizeUrlResponse;
import com.example.muaring.domain.auth.dto.response.LoginResponseDTO;
import com.example.muaring.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "인증 관련 API입니다.")
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login/kakao")
    @Operation(summary = "카카오 소셜로그인 (SDK)", description = "카카오 소셜로그인 로직입니다.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginWithKakao(
            @RequestBody KakaoLoginRequest request
    ) {
        LoginResponseDTO response = authService.kakaoLogin(request);
        return ResponseEntity
                .ok(ApiResponse.ok(response, "카카오 로그인에 성공했습니다."));
    }

    @GetMapping("/spotify/authorization")
    @Operation(summary = "스포티파이 authorize URL 발급", description = "스포티파이 authorize URL 발급 로직입니다.")
    public ResponseEntity<ApiResponse<AuthorizeUrlResponse>> generateAuthorizeUrl() {
        AuthorizeUrlResponse response = authService.generateAuthorizeUrl();
        return ResponseEntity.ok(
                ApiResponse.ok(response, "스포티파이 authorize URL 발급에 성공했습니다."));
    }

    @PostMapping("/login/spotify")
    @Operation(summary = "스포티파이 소셜로그인", description = "스포티파이 소셜로그인 로직입니다.")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginWithSpotify(
            @RequestBody SpotifyLoginRequest request
    ) {
        LoginResponseDTO response = authService.spotifyLogin(request);
        return ResponseEntity
                .ok(ApiResponse.ok(response, "스포티파이 로그인에 성공했습니다."));
    }
}