package com.genius.herewe.core.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청 시, Request body에 담아서 보내야 할 내용")
public record SignupRequest(
	@Schema(description = "회원가입 시 부여받은 UUID token (유효시간 30분)", example = "5f099bb3-6f2a-43b4-be3c-704768307140")
	String token,
	@Schema(description = "사용자가 등록하려는 닉네임", example = "홍길동길동")
	String nickname
) {
}
