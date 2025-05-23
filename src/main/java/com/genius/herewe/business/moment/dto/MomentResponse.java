package com.genius.herewe.business.moment.dto;

import java.time.LocalDateTime;

import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.business.moment.domain.Moment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "모먼트의 정보를 담고 있는 DTO")
public record MomentResponse(
	@Schema(description = "모먼트 식별자(PK)")
	Long momentId,
	@Schema(description = "모먼트 참여 여부")
	Boolean isJoined,
	@Schema(description = "모먼트 마감일자가 지났는지 여부")
	Boolean isClosed,
	@Schema(description = "모먼트 이름")
	String name,
	@Schema(description = "만나는 날짜")
	LocalDateTime meetAt,
	@Schema(description = "만나는 장소에 대한 정보")
	Place place,
	@Schema(description = "현재 모먼트 참여 인원")
	int participantCount,
	@Schema(description = "모먼트 최대 참여 인원")
	int capacity,
	@Schema(description = "모먼트 참여 마감 일자")
	LocalDateTime closedAt
) {

	public static MomentResponse create(Moment moment, Place place, boolean isJoined, boolean isClosed) {
		return MomentResponse.builder()
			.momentId(moment.getId())
			.isJoined(isJoined)
			.isClosed(isClosed)
			.name(moment.getName())
			.meetAt(moment.getMeetAt())
			.place(place)
			.participantCount(moment.getParticipantCount())
			.capacity(moment.getCapacity())
			.closedAt(moment.getClosedAt())
			.build();
	}
}
