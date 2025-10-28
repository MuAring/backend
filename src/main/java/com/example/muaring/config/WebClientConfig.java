package com.example.muaring.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)  // 연결 시도 최대 3초
                .responseTimeout(Duration.ofSeconds(3))  // 응답 대기 최대 3초
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(3, TimeUnit.SECONDS))  // 서버로부터 3초 동안 새로운 데이터 패키를 받지 못하면 연결 끊는다.
                        .addHandlerLast(new WriteTimeoutHandler(3, TimeUnit.SECONDS)));  // 요청 데이터를 서버로 전송할 때 3초 동안 진행이 없으면 타임아웃

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}