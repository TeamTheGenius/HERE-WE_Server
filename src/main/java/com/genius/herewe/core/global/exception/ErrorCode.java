package com.genius.herewe.core.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 가입한 사용자입니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다. 닉네임은 중복될 수 없습니다."),

	JWT_NOT_VALID(HttpStatus.UNAUTHORIZED, "JWT가 유효하지 않습니다."),
	JWT_NOT_FOUND_IN_HEADER(HttpStatus.UNAUTHORIZED, "Header에서 JWT를 찾을 수 없습니다."),
	JWT_NOT_FOUND_IN_COOKIE(HttpStatus.UNAUTHORIZED, "Cookie에서 JWT를 찾을 수 없습니다."),
	REFRESH_NOT_FOUND_IN_DB(HttpStatus.NOT_FOUND, "DB에서 사용자의 Refresh token 정보를 찾을 수 없습니다."),
	TOKEN_HIJACKED(HttpStatus.UNAUTHORIZED, "토큰 탈취가 감지되었습니다."),

	FILE_NOT_EXIST(HttpStatus.NOT_FOUND, "해당 파일이 존재하지 않습니다."),
	FILE_INVALID(HttpStatus.BAD_REQUEST, "파일의 형태가 유효하지 않습니다."),
	NOT_SUPPORTED_EXTENSION(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 확장자입니다."),
	FILE_NOT_SAVED(HttpStatus.INTERNAL_SERVER_ERROR, "파일이 정상적으로 저장되지 않았습니다."),
	FILE_NOT_DELETED(HttpStatus.INTERNAL_SERVER_ERROR, "파일이 정상적으로 삭제되지 않았습니다."),
	NOT_SUPPORTED_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "파일을 받을 수 있는 종류가 아닙니다. profile, crew, moment 중 하나를 입력해주세요."),

	LOAD_PROFILE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "기본 프로필 이미지를 불러오는데에 실패했습니다.");

	private final HttpStatus status;
	private final String message;
}
