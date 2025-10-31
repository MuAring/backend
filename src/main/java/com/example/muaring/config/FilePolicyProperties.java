package com.example.muaring.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "file.policy")
public record FilePolicyProperties (
        Long maxSize,
        int maxNameLength,
        List<String> allowedTypes
) {}