package com.example.muaring.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "Muaring API", version = "1.0.0", description = "Muaring API입니다."),
        security = {@SecurityRequirement(name = "bearerAuth")} // JWT 인증 추가
)
@SecurityScheme(
        name = "bearerAuth", // security requirement 이름과 일치하게 한다.
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",   // 헤더에 "Authorization: Bearer <token>" 형태로 전송
        bearerFormat = "JWT" // 형식 명시 (JWT)
)
public class SwaggerConfig {

    @Bean
    public OpenAPI muaringAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"));
    }
}