package com.genius.herewe.business.crew.dto;

import com.genius.herewe.business.crew.domain.CrewRole;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "크루에 대한 나의 정보 응답 DTO")
public record CrewProfileResponse(
	@Schema(description = "사용자의 닉네임")
	String nickname,
	@Schema(description = "크루에서의 사용자의 권한", example = "LEADER   or   MEMBER")
	CrewRole crewRole
) {
}
