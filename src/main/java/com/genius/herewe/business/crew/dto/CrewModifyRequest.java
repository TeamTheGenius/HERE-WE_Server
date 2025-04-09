package com.genius.herewe.business.crew.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "크루 수정 시 요청 객체")
public record CrewModifyRequest(
	@Schema(description = "수정할 크루 이름", example = "수정된 크루 이름")
	String name,
	@Schema(description = "수정할 크루 소개글", example = "수정된 크루 소개글")
	String introduce
) {
}
