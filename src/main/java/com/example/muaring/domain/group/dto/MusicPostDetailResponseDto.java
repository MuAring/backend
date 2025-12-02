package com.example.muaring.domain.group.dto;

import com.example.muaring.domain.social.entity.MusicPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MusicPostDetailResponseDto {

    // 게시물 정보
    private Long postId;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private boolean isLiked; // 현재 사용자가 좋아요를 눌렀는지
    private LocalDateTime createdAt;

    // 작성자 정보
    private AuthorDto author;

    // 음악 정보
    private MusicDto music;

    // 그룹 정보 (있는 경우)
    private GroupDto group;

    @Getter
    @Builder
    public static class AuthorDto {
        private Long memberId;
        private String nickname;
        private String profileImageUrl;
    }

    @Getter
    @Builder
    public static class MusicDto {
        private Long musicId;
        private String spotifyId;
        private String name;
        private String artistName;
        private String albumName;
        private String albumImgUrl;
        private Integer durationMs;
        private String previewUrl;
    }

    @Getter
    @Builder
    public static class GroupDto {
        private Long groupId;
        private String name;
        private String imageUrl;
    }

    public static MusicPostDetailResponseDto of(MusicPost post, boolean isLiked) {
        return MusicPostDetailResponseDto.builder()
                .postId(post.getId())
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .isLiked(isLiked)
                .createdAt(post.getCreatedAt())
                .author(AuthorDto.builder()
                        .memberId(post.getMember().getId())
                        .nickname(post.getMember().getNickname())
                        .profileImageUrl(post.getMember().getProfileImage() != null
                                ? post.getMember().getProfileImage().getUrl()
                                : null)
                        .build())
                .music(MusicDto.builder()
                        .musicId(post.getMusic().getId())
                        .spotifyId(post.getMusic().getSpotifyId())
                        .name(post.getMusic().getName())
                        .artistName(post.getMusic().getArtistName())
                        .albumName(post.getMusic().getAlbumName())
                        .albumImgUrl(post.getMusic().getAlbumImgUrl())
                        .durationMs(post.getMusic().getDurationMs())
                        .previewUrl(post.getMusic().getPreviewUrl())
                        .build())
                .group(post.getGroup() != null ? GroupDto.builder()
                        .groupId(post.getGroup().getId())
                        .name(post.getGroup().getName())
                        .imageUrl(post.getGroup().getGroupImage())
                        .build() : null)
                .build();
    }
}