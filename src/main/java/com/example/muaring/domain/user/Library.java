package com.example.muaring.domain.user;

import com.example.muaring.domain.music.Music;
import com.example.muaring.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// 사용자별 라이브러리에 저장된 음악
@Entity
@Table(name = "library")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Library {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "library_id")
    private Long libraryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music_id", nullable = false)
    private Music music;

    @Column(length = 20, nullable = false)
    private String category;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
