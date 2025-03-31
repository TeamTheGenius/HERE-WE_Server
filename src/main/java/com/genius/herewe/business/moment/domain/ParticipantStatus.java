package com.genius.herewe.business.moment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipantStatus {
	AVAILABLE("참여가능"),
	PARTICIPATING("참여중"),
	DEADLINE_PASSED("마감");

	private final String value;
}
