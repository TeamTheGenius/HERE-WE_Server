package com.genius.herewe.core.user.dto;

import com.genius.herewe.infra.file.dto.FileResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 성공 시, 응답 객체")
public record SignupResponse(
	@Schema(description = "사용자의 식별자(PK)", example = "1")
	Long userId,
	@Schema(description = "사용자의 닉네임", example = "홍길동길동")
	String nickname,
	@Schema(description = "사용자의 프로필 파일 정보")
	FileResponse fileResponse
) {
}
