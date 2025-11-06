package com.example.muaring.domain.library.controller;

import com.example.muaring.common.response.ApiResponse;
import com.example.muaring.domain.library.dto.LibraryMusicListResponseDTO;
import com.example.muaring.domain.library.service.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
@Validated
public class LibraryController {

    private final LibraryService libraryService;

    @GetMapping
    public ResponseEntity<ApiResponse<LibraryMusicListResponseDTO>> getUserLibrary() {
        LibraryMusicListResponseDTO response = libraryService.getUserLibrary();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.ok(response, "보관함 조회 완료"));
    }

    @PostMapping("add/{musicId}")
    public ResponseEntity<ApiResponse<Void>> addMusicToLibrary(
            @PathVariable Long musicId,
            @RequestParam(required = false, defaultValue = "DEFAULT") String category
    ) {
        libraryService.addMusicToLibrary(musicId, category);
        return ResponseEntity.ok(ApiResponse.ok(null, "보관함에 음악이 추가되었습니다."));
    }

    @DeleteMapping("delete")
    public ResponseEntity<ApiResponse<Void>> deleteMultiple(
            @RequestBody List<Long> musicIds
    ) {
        libraryService.deleteFromLibrary(musicIds);
        return ResponseEntity.ok(ApiResponse.ok(null, "선택한 음악이 삭제되었습니다."));
    }

}
