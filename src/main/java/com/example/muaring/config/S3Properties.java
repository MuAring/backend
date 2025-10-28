package com.example.muaring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws")
public record S3Properties (
        String region,
        Credentials credentials,
        S3 s3
) {
    public record Credentials(String accessKey, String secretKey) {}
    public record S3(String bucket) {}
}