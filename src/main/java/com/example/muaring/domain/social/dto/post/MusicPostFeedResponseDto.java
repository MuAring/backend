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
    private boolean isLiked;          // 내가 좋아요 눌렀는지
    private Integer commentCount;
    private boolean isInLibrary;    // 내 보관함에 있는지 여부
    private LocalDateTime createdAt;

    // 기존 from() : 프로필 URL을 엔티티에서 직접 가져오는 버전
    public static MusicPostFeedResponseDto from(MusicPost post,
                                                boolean isInLibrary,
                                                boolean isLiked) {
        Member member = post.getMember();
        Music music = post.getMusic();

        String profileImageUrl = null;
        if (member.getProfileImage() != null) {
            profileImageUrl = member.getProfileImage().getUrl();
        }

        // 프로필 post의 경우
        Long groupId = null;
        if (post.getGroup() != null) {
            groupId = post.getGroup().getId();
        }

        return MusicPostFeedResponseDto.builder()
                .postId(post.getId())
                .groupId(groupId)

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
                .isLiked(isLiked)               // 내가 좋아요 눌렀는지 여부
                .commentCount(post.getCommentCount())
                .isInLibrary(isInLibrary)   // 내 보관함에 있는지 여부
                .createdAt(post.getCreatedAt())
                .build();
    }

    // 새로 추가된 from(): 외부에서 profileImageUrl을 강제로 넣어주는 버전
    public static MusicPostFeedResponseDto from(
            MusicPost post,
            boolean isInLibrary,
            boolean isLiked,
            String memberProfileImageUrl
    ) {
        Member member = post.getMember();
        Music music = post.getMusic();

        Long groupId = post.getGroup() != null ? post.getGroup().getId() : null;

        return MusicPostFeedResponseDto.builder()
                .postId(post.getId())
                .groupId(groupId)

                // 작성자
                .memberId(member.getId())
                .memberNickname(member.getNickname())
                .memberProfileImageUrl(memberProfileImageUrl)

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
                .isLiked(isLiked)
                .commentCount(post.getCommentCount())
                .isInLibrary(isInLibrary)
                .createdAt(post.getCreatedAt())
                .build();
    }
}