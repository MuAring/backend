package com.example.muaring.domain.auth.client;

import org.springframework.stereotype.Component;

@Component
public class PkceManager {

    private String codeVerifier; // 인메모리 저장

    public void saveCodeVerifier(String codeVerifier) {
        this.codeVerifier = codeVerifier;
    }

    public String getAndRemoveCodeVerifier() {
        String codeVerifier = this.codeVerifier;
        this.codeVerifier = null;  // PKCE는 1회용
        return codeVerifier;
    }
}
