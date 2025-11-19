package com.example.muaring.domain.social.dto.post;

import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.social.entity.MusicPost;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MusicPostFeedResponseDto {

    // 게시글 정보
    private Long postId;
    private Long groupId;

    // 작성자 정보
    private Long memberId;
    private String memberNickname;
    private String memberProfileImageUrl;

    // 음악 정보
    private Long musicId;
    private String spotifyId;
    private String musicName;
    private String artistId;
    private String artistName;
    private String albumName;
    private String albumImgUrl;
    private Integer durationMs;

    // 게시글 메타
    private boolean isProfile;
    private String content;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;

    public static MusicPostFeedResponseDto from(MusicPost post) {
        Member member = post.getMember();
        Music music = post.getMusic();

        String profileImageUrl = null;
        if (member.getProfileImage() != null) {
            profileImageUrl = member.getProfileImage().getUrl();
        }

        return MusicPostFeedResponseDto.builder()
                .postId(post.getId())
                .groupId(post.getGroup().getId())

                // 작성자
                .memberId(member.getId())
                .memberNickname(member.getNickname())
                .memberProfileImageUrl(profileImageUrl)

                // 음악
                .musicId(music.getId())
                .spotifyId(music.getSpotifyId())
                .musicName(music.getName())
                .artistId(music.getArtistId())
                .artistName(music.getArtistName())
                .albumName(music.getAlbumName())
                .albumImgUrl(music.getAlbumImgUrl())
                .durationMs(music.getDurationMs())

                // 포스트 정보
                .isProfile(post.isProfile())
                .content(post.getContent())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())

                .createdAt(post.getCreatedAt())
                .build();
    }
}