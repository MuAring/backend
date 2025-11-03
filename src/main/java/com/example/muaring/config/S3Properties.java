package com.example.muaring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws")
public record S3Properties (
        String region,
        S3 s3
) {
    public record S3(
            String bucket,
            Presign presign,
            Credentials credentials
    ) {
        public record Presign(Integer uploadExpMinutes, Integer downloadExpMinutes) {}
        public record Credentials(String accessKey, String secretKey) {}
        public String bucketUrl(String region) {
            return "https://" + bucket + ".s3." + region + ".amazonaws.com";
        }
    }
}