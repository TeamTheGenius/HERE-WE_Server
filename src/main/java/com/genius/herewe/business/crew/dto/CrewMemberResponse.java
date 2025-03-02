package com.genius.herewe.business.crew.dto;

import java.time.LocalDate;

import com.genius.herewe.business.crew.domain.CrewRole;

import lombok.Builder;

@Builder
public record CrewMemberResponse(
	Long userId,
	String name,
	CrewRole role,
	LocalDate joinedAt
) {
}
