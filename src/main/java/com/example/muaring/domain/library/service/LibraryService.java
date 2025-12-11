package com.example.muaring.domain.library.service;

import com.example.muaring.common.util.SecurityUtil;
import com.example.muaring.domain.library.dto.LibraryMusicDTO;
import com.example.muaring.domain.library.dto.LibraryMusicListResponseDTO;
import com.example.muaring.domain.library.dto.SpotifyExportRequest;
import com.example.muaring.domain.library.entity.Library;
import com.example.muaring.domain.library.repository.LibraryRepository;
import com.example.muaring.domain.member.entity.Member;
import com.example.muaring.domain.member.repository.MemberRepository;
import com.example.muaring.domain.music.entity.Music;
import com.example.muaring.domain.music.exception.MusicErrorCode;
import com.example.muaring.domain.music.exception.MusicException;
import com.example.muaring.domain.music.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final MemberRepository memberRepository;
    private final MusicRepository musicRepository;
    private final SpotifyExportService spotifyExportService;

    public LibraryMusicListResponseDTO getUserLibrary() {

        Long memberId = SecurityUtil.getMemberId();

        if (!memberRepository.existsById(memberId)) {
            throw new MusicException(MusicErrorCode.MEMBER_NOT_FOUND);
        }

        List<Library> libraries = libraryRepository.findByMemberIdOrderByCreatedAtAsc(memberId);

        List<LibraryMusicDTO> musicList = libraries.stream()
                .map(library -> LibraryMusicDTO.builder()
                        .libraryId(library.getId())
                        .musicId(library.getMusic().getId())
                        .title(library.getMusic().getName())
                        .artist(library.getMusic().getArtistName())
                        .albumImage(library.getMusic().getAlbumImgUrl())
                        .createdAt(library.getCreatedAt())
                        .build()
                )
                .toList();

        return LibraryMusicListResponseDTO.builder()
                .totalMusicCount(musicList.size())
                .musicList(musicList)
                .build();
    }

    @Transactional
    public LibraryMusicDTO addMusicToLibrary(Long musicId, String category) {

        Long memberId = SecurityUtil.getMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MusicException(MusicErrorCode.MEMBER_NOT_FOUND));


        Music music = musicRepository.findById(musicId)
                .orElseThrow(() -> new MusicException(MusicErrorCode.MUSIC_NOT_FOUND));

        if (libraryRepository.existsByMemberIdAndMusicId(memberId, musicId)) {
            throw new MusicException(MusicErrorCode.MUSIC_ALREADY_EXISTS);

        }

        Library library = Library.builder()
                .member(member)
                .music(music)
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        Library savedLibrary = libraryRepository.save(library);

        return LibraryMusicDTO.builder()
                .libraryId(library.getId())
                .musicId(savedLibrary.getMusic().getId())
                .title(savedLibrary.getMusic().getName())
                .artist(savedLibrary.getMusic().getArtistName())
                .albumImage(savedLibrary.getMusic().getAlbumImgUrl())
                .createdAt(savedLibrary.getCreatedAt())
                .build();
    }

    @Transactional
    public void deleteFromLibrary(List<Long> libraryIds) {

        if (libraryIds == null || libraryIds.isEmpty()) {
            throw new MusicException(MusicErrorCode.MUSIC_NOT_FOUND);
        }


        Long memberId = SecurityUtil.getMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MusicException(MusicErrorCode.MEMBER_NOT_FOUND));

        if (!libraryRepository.existsByIdsAndMember(libraryIds, member)) {
            throw new MusicException(MusicErrorCode.MUSIC_NOT_FOUND);
        }

        libraryRepository.deleteByIdsAndMember(libraryIds, member);

    }

    @Transactional
    public void exportToSpotify(SpotifyExportRequest request) {

        String token = request.getSpotifyAccessToken();
        List<Long> musicIds = request.getMusicIds();
        List<Music> musicList = musicRepository.findAllById(musicIds);

        if (musicList.isEmpty()) {
            throw new MusicException(MusicErrorCode.MUSIC_NOT_FOUND);
        }

        List<String> trackIds = musicList.stream()
                .map(Music::getSpotifyId)
                .toList();

        spotifyExportService.exportTracks(token, trackIds);
    }
}
