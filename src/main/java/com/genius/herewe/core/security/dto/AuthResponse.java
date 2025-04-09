package com.genius.herewe.core.security.dto;

import com.genius.herewe.infra.file.dto.FileResponse;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "JWT 인가 이후 전달받는 사용자 정보")
public record AuthResponse(
	@Schema(description = "사용자의 식별자(PK)", example = "1")
	Long userId,
	@Schema(description = "사용자의 닉네임", example = "홍길동길동")
	String nickname,
	@Schema(description = "사용자의 프로필 파일 정보")
	FileResponse fileResponse
) {
}
