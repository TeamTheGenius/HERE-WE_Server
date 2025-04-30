package com.genius.herewe.core.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인가 요청 시 필요한 사용자 정보")
public record AuthRequest(
	@Schema(description = "사용자 식별자(PK)", example = "1")
	Long userId,
	@Schema(description = "인가 토큰", example = "7053658f-b32b-4fe1-84da-73d654907b12")
	String token
) {
}
