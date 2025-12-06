package com.example.muaring.common.util;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
// ✨ PKCE 표준에 따라 code_verifier을 생성하고 code_challenge로 해시 처리하는 유틸리티 클래스
public class PkceUtil {

    public record Pkce(
            String codeVerifier,
            String codeChallenge
    ) {
        public static Pkce generatePkce() {
            try {
                String codeVerifier = generateCodeVerifier();
                log.info("codeverifier: {}", codeVerifier);
                String codeChallenge = generateCodeChallenge(codeVerifier);
                return new Pkce(codeVerifier, codeChallenge);

            } catch (Exception e) {
                throw new RuntimeException("PKCE 생성에 실패했습니다.", e);
            }
        }
    }

    private static String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] code = new byte[32];
        secureRandom.nextBytes(code);
        System.out.println("VERIFIER =  " + Base64.getUrlEncoder().withoutPadding().encodeToString(code));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(code);
    }

    private static String generateCodeChallenge(String codeVerifier) throws Exception {
        byte[] bytes = codeVerifier.getBytes(StandardCharsets.US_ASCII);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(bytes, 0, bytes.length);
        byte[] digest = md.digest();

        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
