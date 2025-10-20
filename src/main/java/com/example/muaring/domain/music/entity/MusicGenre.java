package com.example.muaring.domain.music.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "musicGenre")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicGenre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "music_genre_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_id", nullable = false)
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;
}
