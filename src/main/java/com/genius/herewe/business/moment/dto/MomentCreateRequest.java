package com.genius.herewe.business.moment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.genius.herewe.business.location.search.dto.Place;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "모먼트 생성 시 request body")
public record MomentCreateRequest(
	@Schema(description = "모먼트를 생성할 크루의 식별자")
	Long crewId,
	@Schema(description = "모먼트 이름")
	String momentName,
	@Schema(description = "모먼트 약속 일자(날짜/시간)")
	LocalDateTime meetAt,
	@Schema(description = "모먼트의 만나는 위치")
	Place meetPlace,
	@Schema(description = "마감 인원 수")
	int capacity,
	@Schema(description = "마감 날짜/시간")
	LocalDate closedAt
) {
}
