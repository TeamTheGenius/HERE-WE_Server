package com.genius.herewe.business.moment.dto;

import java.time.LocalDateTime;

import com.genius.herewe.business.moment.domain.Moment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "모먼트 미리보기 응답 객체")
public record MomentResponse(
	@Schema(description = "모먼트 식별자(PK)")
	Long momentId,
	@Schema(description = "사용자의 모먼트 참여 여부")
	Boolean isJoined,
	@Schema(description = "모먼트 이름")
	String name,
	@Schema(description = "현재 모먼트 참여 인원")
	int participantCount,
	@Schema(description = "모먼트 최대 참여 인원")
	int capacity,
	@Schema(description = "모먼트 참여 마감 일자")
	LocalDateTime closedAt
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
