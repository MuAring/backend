package com.example.muaring.domain.library.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LibraryMusicListResponseDTO {

    private int totalMusicCount;
    private List<LibraryMusicDTO> musicList;
}
