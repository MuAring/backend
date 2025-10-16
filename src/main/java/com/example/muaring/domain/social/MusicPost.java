package com.example.muaring.domain.social;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.user.User;
import com.example.muaring.domain.music.Music;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "music_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;
}
