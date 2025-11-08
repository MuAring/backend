package com.example.muaring.domain.social.dto.like.response;

public record LikeResponseDTO(
        Long postId,
        int numOfLikes,
        boolean liked
) {
    public static LikeResponseDTO of(Long postId, Integer likeCount, boolean liked) {
        return new LikeResponseDTO(postId, likeCount, liked);
    }
}
