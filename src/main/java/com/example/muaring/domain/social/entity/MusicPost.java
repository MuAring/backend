package com.example.muaring.domain.social.entity;

import com.example.muaring.domain.common.BaseEntity;
import com.example.muaring.domain.group.entity.Group;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.music.entity.Music;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "music_post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MusicPost extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(name = "is_profile", nullable = false)
    private boolean isProfile;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "comment_count", nullable = false)
    private Integer commentCount = 0;
}
