package com.example.muaring.domain.library.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpotifyExportRequest {
    private List<Long> musicIds;
}
