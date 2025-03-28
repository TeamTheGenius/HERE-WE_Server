package com.genius.herewe.business.location.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "")
public record PlaceResponse(
	@Schema(description = "장소를 조회한 모먼트의 식별자")
	Long momentId,
	@Schema(description = "장소 정보 리스트")
	List<LocationInfo> locationInfos,
	@Schema(description = "장소의 개수", example = "17")
	int count
) {

	public static PlaceResponse create(Long momentId, List<LocationInfo> locationInfos) {
		return PlaceResponse.builder()
			.momentId(momentId)
			.locationInfos(locationInfos)
			.count(locationInfos.size())
			.build();
	}
}
