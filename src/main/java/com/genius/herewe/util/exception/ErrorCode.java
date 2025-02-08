package com.genius.herewe.util.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 파일이 존재하지 않습니다."),
	FILE_INVALID(HttpStatus.BAD_REQUEST, "파일의 형태가 유효하지 않습니다."),
	NOT_SUPPORTED_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 확장자입니다."),
	FILE_NOT_SAVED(HttpStatus.BAD_REQUEST, "파일이 정상적으로 저장되지 않았습니다."),
	FILE_NOT_DELETED(HttpStatus.BAD_REQUEST, "파일이 정상적으로 삭제되지 않았습니다."),
	;

	private final HttpStatus status;
	private final String message;
}
