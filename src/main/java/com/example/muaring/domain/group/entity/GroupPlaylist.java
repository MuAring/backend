package com.example.muaring.domain.group.entity;

import com.example.muaring.domain.music.entity.Music;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "group_playlist")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPlaylist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_playlist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
