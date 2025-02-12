package com.genius.herewe.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청 시, Request body에 담아서 보내야 할 내용")
public record SignupRequest(
	@Schema(description = "사용자의 식별자(PK)", example = "1")
	Long userId,
	@Schema(description = "사용자가 등록하려는 닉네임", example = "홍길동길동")
	String nickname
) {
}
