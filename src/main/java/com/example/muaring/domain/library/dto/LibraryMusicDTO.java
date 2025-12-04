package com.example.muaring.domain.library.dto;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryMusicDTO {
    private Long libraryId;
    private Long musicId;
    private String title;
    private String artist;
    private String albumImage;
    private LocalDateTime createdAt;
}
