package com.example.muaring.domain.music.repository;

import com.example.muaring.domain.social.entity.MusicPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MusicPostRepository extends JpaRepository<MusicPost, Long> {
}
