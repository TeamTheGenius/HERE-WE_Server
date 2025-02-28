package com.genius.herewe.business.crew.dto;

import com.genius.herewe.business.crew.domain.Crew;

public record CrewResponse(
	Long crewId,
	String name,
	int participantCount
) {

	public static CrewResponse create(Crew crew) {
		return new CrewResponse(crew.getId(), crew.getName(), crew.getParticipantCount());
	}
}
