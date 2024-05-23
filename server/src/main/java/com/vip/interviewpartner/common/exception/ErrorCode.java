package com.vip.interviewpartner.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 애플리케이션에서 사용할 오류 코드를 정의하는 열거형입니다. 각 코드는 오류를 구분하는 유니크한 메시지와 HTTP 상태 코드를 포함합니다.
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_REQUEST(400, "유효하지 않는 요청입니다."),
    AUTHENTICATION_REQUIRED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    SERVER_ERROR(500, "서버에 에러가 발생하였습니다."),
    DUPLICATE_EMAIL(409, "Email is already in use."),
    DUPLICATE_NICKNAME(409, "Nickname is already in use."),
    LOGIN_FAILURE(401, "로그인에 실패했습니다."),
    ACCESS_TOKEN_EXPIRED(401, "엑세스 토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(401, "리프레쉬 토큰이 만료되었습니다."),
    INVALID_TOKEN(401, "유효하지 않는 토큰입니다."),
    REFRESH_TOKEN_NOT_EXIST(401, "리프레쉬 토큰이 존재하지 않습니다."),
    FILE_EMPTY(400, "파일이 존재하지 않습니다."),
    UPLOAD_FAILURE(400, "업로드에 실패했습니다."),
    INVALID_FILE_TYPE(415, "PDF 파일만 업로드 가능합니다."),
    MEMBER_NOT_FOUND(400, "유효하지 않은 멤버입니다."),
    UNSUPPORTED_SOCIAL_MEDIA(400, "지원하지 않는 소셜 미디어입니다."),
    INVALID_FIREBASE_ID_TOKEN(400, "유효하지 않는 Firebase 토큰입니다."),
    DUPLICATE_TAG_NAME(409, "이미 존재하는 태그 이름입니다."),
    RESOURCE_NOT_FOUND(404, "요청한 리소스가 존재하지 않습니다.");

    private final int status;
    private final String message;
}
