package com.genius.herewe.business.crew.dto;

import lombok.Builder;

@Builder
public record CrewExpelRequest(
	Long crewId,
	String targetName
) {
}
