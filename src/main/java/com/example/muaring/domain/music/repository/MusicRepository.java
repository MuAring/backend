package com.example.muaring.domain.music.repository;

import com.example.muaring.domain.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MusicRepository extends JpaRepository<Music, Long> {

    Optional<Music> findBySpotifyId(String spotifyId);

}
