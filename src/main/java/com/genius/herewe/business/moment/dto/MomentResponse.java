package com.genius.herewe.business.moment.dto;

import java.time.LocalDate;

import com.genius.herewe.business.moment.domain.Moment;

import lombok.Builder;

@Builder
public record MomentResponse(
	Long momentId,
	Boolean isJoined,
	String name,
	int participantCount,
	int capacity,
	LocalDate closedAt
) {

	public static MomentResponse createJoined(Moment moment, boolean isJoined) {
		return MomentResponse.builder()
			.momentId(moment.getId())
			.isJoined(isJoined)
			.name(moment.getName())
			.participantCount(moment.getParticipantCount())
			.capacity(moment.getCapacity())
			.closedAt(moment.getClosedAt())
			.build();
	}
}
