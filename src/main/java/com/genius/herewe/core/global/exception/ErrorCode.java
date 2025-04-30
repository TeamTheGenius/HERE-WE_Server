package com.genius.herewe.core.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	UNEXPECTED_ERROR(HttpStatus.BAD_REQUEST, "처리되지 않은 에러입니다. 추가 처리가 필요합니다."),

	CONCURRENT_MODIFICATION_EXCEPTION(HttpStatus.CONFLICT, "이 데이터는 다른 사용자에 의해 수정되었습니다. 다시 시도해주세요."),
	REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "해당 항목은 필수 항목입니다. 입력 값을 확인해주세요."),
	VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "데이터 전달 관련 유효성 검사에 실패했습니다."),
	INVALID_PAGINATION_PARAM(HttpStatus.BAD_REQUEST, "페이지 번호는 0 이상이어야 하며, 페이지 사이즈는 1~15 중 하나여야 합니다."),

	INVALID_KEYWORD(HttpStatus.BAD_REQUEST, "검색어는 필수 항목입니다."),
	INVALID_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "요청 파라미터 확인 후 재시도해주세요."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API(Kakao) 서버 에러. 잠시 후 다시 시도해주세요."),
	SERVICE_IN_MAINTENANCE(HttpStatus.SERVICE_UNAVAILABLE, "외부 API(Kakao) 서버가 점검 중입니다."),

	ALREADY_REGISTERED(HttpStatus.CONFLICT, "이미 가입한 사용자입니다."),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 닉네임입니다. 닉네임은 중복될 수 없습니다."),
	INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "닉네임은 2한글, 영문자, 숫자만 가능하며 2~20자여야 합니다."),

	CREW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 CREW를 찾을 수 없습니다."),
	LEADER_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "CREW LEADER의 권한이 필요합니다."),
	LEADER_CANNOT_EXPEL(HttpStatus.BAD_REQUEST, "CREW LEADER는 CREW에서 탈퇴할 수 없습니다."),

	CREW_JOIN_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 크루에 대한 참여 정보가 없습니다."),
	ALREADY_JOINED_CREW(HttpStatus.BAD_REQUEST, "이미 참여한 크루입니다."),
	CREW_MEMBERSHIP_REQUIRED(HttpStatus.FORBIDDEN, "크루 멤버에게만 허용된 작업입니다. 크루에 참여 후 재시도해주세요."),
	INVALID_CREW_CAPACITY(HttpStatus.BAD_REQUEST, "CREW의 최대 참여 가능 인원은 500명 이하여야 합니다."),

	INVITATION_NOT_FOUND(HttpStatus.NOT_FOUND, "크루 초대 정보를 찾을 수 없습니다. 초대 정보를 다시 확인해주세요."),
	INVITATION_EXPIRED(HttpStatus.BAD_REQUEST, "크루 초대가 만료되었습니다."),

	MOMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 MOMENT를 찾을 수 없습니다."),
	INVALID_MOMENT_CAPACITY(HttpStatus.BAD_REQUEST, "MOMENT의 최대 참여 가능 인원은 2명 이상, 500명 이하여야 합니다."),
	INVALID_MOMENT_DATE(HttpStatus.BAD_REQUEST, "만남일자(meetAt)/마감일자(closedAt)는 오늘보다 나중이어야 하며, 만남일자가 마감일자보다 더 이후여야 합니다."),
	ALREADY_JOINED_MOMENT(HttpStatus.BAD_REQUEST, "이미 참여한 모먼트입니다."),
	MOMENT_DEADLINE_EXPIRED(HttpStatus.BAD_REQUEST, "모먼트 참여 마감일자가 지났습니다. 모먼트 참여/참여 취소는 마감일자 이전까지 가능합니다."),
	MOMENT_CAPACITY_FULL(HttpStatus.BAD_REQUEST, "참여 인원이 최대 정원에 도달했습니다."),

	MOMENT_PARTICIPATION_NOT_FOUND(HttpStatus.FORBIDDEN, "모먼트 참여 정보를 찾을 수 없습니다. 모먼트 참여자가 아닙니다."),

	LOCATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "해당 순서의 장소가 이미 추가되었습니다. 잠시 후 다시 시도해주세요."),
	LOCATION_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "장소는 최대 100개까지 등록할 수 있습니다."),
	INVALID_LOCATION_INDEX(HttpStatus.BAD_REQUEST, "유효하지 않은 위치 인덱스입니다. 다시 확인해주세요."),

	UNAUTHORIZED_ISSUE(HttpStatus.UNAUTHORIZED, "회원가입이 되어 있지 않은 사용자의 경우 JWT를 발급할 수 없습니다."),
	JWT_NOT_VALID(HttpStatus.UNAUTHORIZED, "JWT가 유효하지 않습니다."),
	JWT_NOT_FOUND_IN_HEADER(HttpStatus.UNAUTHORIZED, "Header에서 JWT를 찾을 수 없습니다."),
	JWT_NOT_FOUND_IN_COOKIE(HttpStatus.UNAUTHORIZED, "Cookie에서 JWT를 찾을 수 없습니다."),
	REFRESH_NOT_FOUND_IN_DB(HttpStatus.UNAUTHORIZED, "DB에서 사용자의 Refresh token 정보를 찾을 수 없습니다."),
	TOKEN_HIJACKED(HttpStatus.UNAUTHORIZED, "토큰 탈취가 감지되었습니다. 다시 로그인해주세요."),

	TOKEN_NOT_FOUND_IN_REDIS(HttpStatus.UNAUTHORIZED, "Redis에서 저장되어 있는 토큰을 찾을 수 없습니다."),

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
