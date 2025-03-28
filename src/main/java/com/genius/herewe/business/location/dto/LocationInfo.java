package com.genius.herewe.business.location.dto;

import com.genius.herewe.business.location.domain.Location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "")
public record LocationInfo(
	@Schema(description = "")
	int index,
	@Schema(description = "")
	Long placeId,
	@Schema(description = "")
	String name,
	@Schema(description = "")
	String address,
	@Schema(description = "")
	String roadAddress,
	@Schema(description = "")
	String url,
	@Schema(description = "")
	Double x,
	@Schema(description = "")
	Double y,
	@Schema(description = "")
	String phone
) {

	public static LocationInfo create(Location location) {
		return LocationInfo.builder()
			.index(location.getLocationIndex())
			.placeId(location.getPlaceId())
			.name(location.getName())
			.address(location.getAddress())
			.roadAddress(location.getRoadAddress())
			.url(location.getUrl())
			.x(location.getX())
			.y(location.getY())
			.phone(location.getPhone())
			.build();
	}
}
