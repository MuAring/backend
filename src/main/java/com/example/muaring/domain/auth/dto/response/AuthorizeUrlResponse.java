package com.example.muaring.domain.auth.dto.response;

public record AuthorizeUrlResponse(
        String authorizeUrl
) {
    public static AuthorizeUrlResponse create(String url) {
        return new AuthorizeUrlResponse(url);
    }
}
