package com.example.muaring.domain.music.exception;

import com.example.muaring.common.response.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum MusicErrorCode implements ErrorCode {

    INVALID_MUSIC_REQUEST(4001, HttpStatus.BAD_REQUEST, "잘못된 음악 요청입니다."),

    MEMBER_NOT_FOUND(4003, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    MUSIC_NOT_FOUND(4004, HttpStatus.NOT_FOUND, "존재하지 않는 음악입니다."),
    GROUP_NOT_FOUND(4005, HttpStatus.NOT_FOUND, "존재하지 않는 그룹입니다."),

    SPOTIFY_AUTH_FAILED(4006, HttpStatus.INTERNAL_SERVER_ERROR, "Spotify access token 발급 실패"),
    SPOTIFY_SEARCH_FAILED(4007, HttpStatus.INTERNAL_SERVER_ERROR, "Spotify 음악 검색 실패"),
    SPOTIFY_NO_RESULTS(4008, HttpStatus.NOT_FOUND, "Spotify에서 검색 결과를 찾을 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
