package com.example.muaring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.image")
public record ImageProperties(DefaultImageProperties defaultProfile) {
    public record DefaultImageProperties(
            String s3Key,
            String fileName,
            String fileType,
            Long fileSize
    ) {}
}