package com.example.muaring.domain.music.service;

import com.example.muaring.domain.music.dto.MusicResponseDTO;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.repository.MusicRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MusicService {
    private final MusicRepository musicRepository;

    public List<MusicResponseDTO> searchMusic(String query) {
        List<Music> musicList = musicRepository.findByNameContainingIgnoreCaseOrArtistNameContainingIgnoreCase(query, query);
        return musicList.stream()
                .map(music -> new MusicResponseDTO(
                        music.getId(),
                        music.getSpotifyId(),
                        music.getName(),
                        music.getArtistId(),
                        music.getArtistName(),
                        music.getAlbumName(),
                        music.getAlbumImgUrl(),
                        music.getPopularity()
                ))
                .collect(Collectors.toList());
    }
}