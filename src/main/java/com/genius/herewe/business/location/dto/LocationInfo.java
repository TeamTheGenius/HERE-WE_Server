package com.genius.herewe.business.location.dto;

import com.genius.herewe.business.location.domain.Location;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "장소 ")
public record LocationInfo(
	@Schema(description = "장소의 순서")
	int index,
	@Schema(description = "kakao에서 제공하는 장소 식별자")
	Long placeId,
	@Schema(description = "장소 이름")
	String name,
	@Schema(description = "지번 주소")
	String address,
	@Schema(description = "도로명 주소")
	String roadAddress,
	@Schema(description = "해당 장소의 kakao map url")
	String url,
	@Schema(description = "해당 장소의 x 좌표")
	Double x,
	@Schema(description = "해당 장소의 y 좌표")
	Double y,
	@Schema(description = "전화번호")
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
