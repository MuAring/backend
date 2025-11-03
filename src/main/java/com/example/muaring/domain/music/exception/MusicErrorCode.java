package com.example.muaring.domain.music.exception;

import com.example.muaring.common.response.ErrorCode;
import lombok.*;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum MusicErrorCode implements ErrorCode {

    // 400 Bad
    INVALID_MUSIC_REQUEST(4001, HttpStatus.BAD_REQUEST, "잘못된 음악 요청입니다."),
    MUSIC_POST_CONTENT_EMPTY(4002, HttpStatus.BAD_REQUEST, "음악 게시글 내용을 입력해주세요."),

    MEMBER_NOT_FOUND(4003, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    MUSIC_NOT_FOUND(4004, HttpStatus.NOT_FOUND, "존재하지 않는 음악입니다."),
    GROUP_NOT_FOUND(4005, HttpStatus.NOT_FOUND, "존재하지 않는 그룹입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
