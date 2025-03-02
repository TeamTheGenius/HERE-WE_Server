package com.genius.herewe.business.crew.dto;

import com.genius.herewe.business.crew.domain.Crew;
import com.genius.herewe.business.crew.domain.CrewRole;

import lombok.Builder;

@Builder
public record CrewResponse(
	Long crewId,
	String name,
	String leaderName,
	CrewRole role,
	String introduce,
	int participantCount
) {

	public static CrewResponse create(Crew crew, CrewRole role) {
		return CrewResponse.builder()
			.crewId(crew.getId())
			.name(crew.getName())
			.leaderName(crew.getLeaderName())
			.role(role)
			.introduce(crew.getIntroduce())
			.participantCount(crew.getParticipantCount())
			.build();
	}
}
