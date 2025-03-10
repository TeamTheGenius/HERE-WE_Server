package com.genius.herewe.business.crew.dto;

import com.genius.herewe.business.crew.domain.Crew;

public record CrewPreviewResponse(
	Long crewId,
	String name,
	int participantCount
) {

	public static CrewPreviewResponse create(Crew crew) {
		return new CrewPreviewResponse(crew.getId(), crew.getName(), crew.getParticipantCount());
	}
}
