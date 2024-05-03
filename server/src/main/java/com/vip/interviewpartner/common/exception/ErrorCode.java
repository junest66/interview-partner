package com.vip.interviewpartner.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 애플리케이션에서 사용할 오류 코드를 정의하는 열거형입니다.
 * 각 코드는 오류를 구분하는 유니크한 메시지와 HTTP 상태 코드를 포함합니다.
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    INVALID_REQUEST(400, "유효하지 않는 요청입니다."),
    AUTHENTICATION_REQUIRED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "권한이 없습니다."),
    SERVER_ERROR(500, "서버에 에러가 발생하였습니다.");

    private final int status;
    private final String message;
}