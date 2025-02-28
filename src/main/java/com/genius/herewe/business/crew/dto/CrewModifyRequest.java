package com.genius.herewe.business.crew.dto;

public record CrewModifyRequest(
	Long crewId,
	String name,
	String introduce
) {
}
