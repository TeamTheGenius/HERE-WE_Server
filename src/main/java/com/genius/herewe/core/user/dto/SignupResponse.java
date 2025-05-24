package com.genius.herewe.core.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 성공 시, 응답 객체")
public record SignupResponse(
	@Schema(description = "사용자의 식별자(PK)", example = "1")
	Long userId,
	@Schema(description = "인가 토큰", example = "7053658f-b32b-4fe1-84da-73d654907b12")
	String token
) {
}
