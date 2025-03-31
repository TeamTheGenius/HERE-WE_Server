package com.genius.herewe.business.moment.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "다가오는 모먼트 응답 객체")
public record MomentIncomingResponse(
	@Schema(description = "모먼트 식별자(PK)", example = "1")
	Long momentId,
	@Schema(description = "모먼트가 속한 크루의 이름", example = "눈물나게 맛있는거 먹는 사람들")
	String crewName,
	@Schema(description = "모먼트의 이름", example = "눈물나게 맛있는 훠궈 먹으러 가는 날")
	String momentName,
	@Schema(description = "모먼트 만남일자", example = "2025-04-28T12:31:00")
	LocalDateTime meetAt,
	@Schema(description = "모먼트 만남 장소", example = "하이디라오 강남점")
	String meetPlaceName
) {
}
