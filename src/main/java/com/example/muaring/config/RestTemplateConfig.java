package com.example.muaring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

// ✨외부 서버(카카오, 스포티파이)와의 HTTP 통신용 RestTemplate 전역 설정 클래스
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}