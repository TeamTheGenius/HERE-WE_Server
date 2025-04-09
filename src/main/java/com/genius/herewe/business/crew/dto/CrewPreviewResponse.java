package com.genius.herewe.business.crew.dto;

import com.genius.herewe.business.crew.domain.Crew;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "크루의 간략한 정보를 담은 응답 객체")
public record CrewPreviewResponse(
	@Schema(description = "크루의 식별자(PK)", example = "1")
	Long crewId,
	@Schema(description = "크루의 이름", example = "눈물나게 맛있는거 먹고싶은 사람들")
	String name,
	@Schema(description = "현재까지의 크루 참여 인원", example = "13")
	int participantCount
) {

	public static CrewPreviewResponse create(Crew crew) {
		return new CrewPreviewResponse(crew.getId(), crew.getName(), crew.getParticipantCount());
	}
}
