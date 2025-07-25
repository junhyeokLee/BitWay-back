package com.example.bitway_back.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

	// 400
    BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    NOT_FOUND_DATA(HttpStatus.BAD_REQUEST.value(), "존재하지 않는 데이터 입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST.value(), "중복된 이메일 입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST.value(), "중복된 닉네임 입니다."),

    // 401
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "아이디 혹은 비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED_REQUEST(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다."),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED.value(), "Account Locked"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "Invalid Token"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(), "Invalid Password"),

    // 403
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN.value(), "접근 권한이 없습니다."),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND.value(), "요청을 찾을 수 없습니다."),

    // 405
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED.value(), "지원하지 않는 요청입니다."),

    // 408
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT.value(), "요청 시간이 초과되었습니다."),

    // 409
    LOGIN_CONFLICT(HttpStatus.CONFLICT.value(), "이미 동일한 ID로 로그인 중입니다. 다른 장치에서 로그아웃한 후 다시 시도해 주세요."),
    DUPLICATE_NAME(HttpStatus.CONFLICT.value(), "이미 사용 중인 이름입니다."),
    DUPLICATE_CODE(HttpStatus.CONFLICT.value(), "이미 사용 중인 코드입니다."),

    // 500
    SQL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "데이터베이스 처리과정에서 오류가 발생했습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부에서 오류가 발생했습니다.");

    private final int status;
    private final String msg;

    ErrorCode(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
