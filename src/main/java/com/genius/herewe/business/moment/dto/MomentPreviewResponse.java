package com.genius.herewe.business.moment.dto;

import java.time.LocalDateTime;

import com.genius.herewe.business.moment.domain.Moment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "크루 내 모먼트 탭에서 모먼트 목록 조회 시 응답 DTO, 간략한 정보 제공")
public record MomentPreviewResponse(
	@Schema(description = "모먼트의 식별자(PK)", example = "1")
	Long momentId,
	@Schema(description = "모먼트 참여 여부")
	Boolean isJoined,
	@Schema(description = "모먼트 마감일자가 지났는지 여부")
	Boolean isClosed,
	@Schema(description = "모먼트의 이름", example = "눈물나게 맛있는 훠궈 먹으러 가자")
	String name,
	@Schema(description = "모먼트 만남일자", example = "2025-04-28T12:31:00")
	LocalDateTime meetAt,
	@Schema(description = "만남 장소 이름", example = "하이디라오 강남점")
	String meetingPlaceName,
	@Schema(description = "현재까지 참여한 인원의 수", example = "5")
	Integer participantCount,
	@Schema(description = "참여 가능한 최대 정원", example = "20")
	Integer capacity,
	@Schema(description = "모먼트 참여 마감 일자", example = "2025-04-05T12:31:00")
	LocalDateTime closedAt
) {

	public static MomentPreviewResponse create(Moment moment, Boolean isJoined, Boolean isClosed, String placeName) {
		return MomentPreviewResponse.builder()
			.momentId(moment.getId())
			.isJoined(isJoined)
			.isClosed(isClosed)
			.name(moment.getName())
			.meetAt(moment.getMeetAt())
			.meetingPlaceName(placeName)
			.participantCount(moment.getParticipantCount())
			.capacity(moment.getCapacity())
			.closedAt(moment.getClosedAt())
			.build();
	}
}
