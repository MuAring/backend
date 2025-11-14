package com.example.muaring.domain.member.response;

import com.example.muaring.common.response.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    // 400 에러
    ALREADY_HAS_PROFILE(2001, HttpStatus.BAD_REQUEST, "이미 프로필 정보가 존재하는 회원입니다."),
    EMPTY_PROFILE_UPDATE_REQUEST(2002, HttpStatus.BAD_REQUEST, "변경할 프로필 정보가 없습니다."),

    // 404 에러
    MEMBER_NOT_FOUND(2003, HttpStatus.NOT_FOUND, "해당 사용자를 찾을 수 없습니다."),
    METRICS_NOT_FOUND(2004, HttpStatus.NOT_FOUND, "핵심 필드를 찾을 수 없습니다."),
    FOLLOWER_NOT_FOUND(2005, HttpStatus.NOT_FOUND, "팔로우 요청자가 존재하지 않습니다."),
    FOLLOWEE_NOT_FOUND(2006, HttpStatus.NOT_FOUND, "팔로우 대상자가 존재하지 않습니다."),
    FOLLOW_REQUEST_NOT_FOUND(2007, HttpStatus.NOT_FOUND, "팔로우 대상자가 존재하지 않습니다."),

    // 409 에러
    METRICS_CONFLICT(2008, HttpStatus.CONFLICT, "핵심 필드가 존재할 수 없습니다."),
    MEMBER_CONFLICT(2009,HttpStatus.CONFLICT, "본인 계정에 대해 작업을 수행할 수 없습니다."),
    FOLLOW_ALREADY_EXISTS(2010, HttpStatus.CONFLICT, "이미 팔로우 중입니다."),
    FOLLOW_REQUEST_ALREADY_EXISTS(2011, HttpStatus.CONFLICT, "이미 팔로우 요청을 보냈습니다."),
    UNAUTHORIZED_ACTION(2012, HttpStatus.CONFLICT, "본인 요청만 승인할 수 있습니다"),
    DUPLICATE_NICKNAME(2013, HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다.")
    ;

    private final int code;
    private final HttpStatus status;
    private final String message;
}
