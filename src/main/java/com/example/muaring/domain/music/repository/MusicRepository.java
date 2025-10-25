package com.example.muaring.domain.music.repository;

import com.example.muaring.domain.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MusicRepository extends JpaRepository<Music, Long> {
    List<Music> findByNameContainingIgnoreCaseOrArtistNameContainingIgnoreCase(String name, String artistName);
}
