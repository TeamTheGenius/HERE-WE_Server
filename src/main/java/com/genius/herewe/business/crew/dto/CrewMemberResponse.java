package com.genius.herewe.business.crew.dto;

import java.time.LocalDate;

import com.genius.herewe.business.crew.domain.CrewMember;
import com.genius.herewe.business.crew.domain.CrewRole;

import lombok.Builder;

@Builder
public record CrewMemberResponse(
	Long userId,
	String name,
	CrewRole role,
	LocalDate joinedAt
) {

	public static CrewMemberResponse create(CrewMember crewMember) {
		return CrewMemberResponse.builder()
			.userId(crewMember.getUser().getId())
			.name(crewMember.getUser().getNickname())
			.role(crewMember.getRole())
			.joinedAt(crewMember.getJoinedAt())
			.build();
	}
}
