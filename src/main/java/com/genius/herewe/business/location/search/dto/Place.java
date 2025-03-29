package com.genius.herewe.business.location.search.dto;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.genius.herewe.business.location.domain.Location;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "Map API 결과를 담을 객체")
public record Place(
	@NotNull
	@Schema(description = "장소 식별자")
	Long id,
	@NotNull
	@Schema(description = "이름")
	@JsonProperty(value = "place_name")
	String name,
	@NotNull
	@Schema(description = "해당 장소의 x 좌표")
	@JsonProperty(value = "x")
	Double x,
	@NotNull
	@Schema(description = "해당 장소의 y 좌표")
	@JsonProperty(value = "y")
	Double y,
	@NotNull
	@Schema(description = "지번 주소")
	@JsonProperty(value = "address_name")
	String address,
	@NotNull
	@Schema(description = "도로명 주소")
	@JsonProperty(value = "road_address_name")
	String roadAddress,
	@NotNull
	@Schema(description = "kakao map url")
	@JsonProperty(value = "place_url")
	String url,
	@NotNull
	@Schema(description = "전화번호")
	@JsonProperty(value = "phone")
	String phone
) {

	public static Place createFromOptional(Optional<Location> optionalLocation) {
		if (optionalLocation.isEmpty()) {
			return new Place(null, null, null, null, null, null, null, null);
		}
		return Place.create(optionalLocation.get());
	}

	public static Place create(Location location) {
		return Place.builder()
			.id(location.getPlaceId())
			.name(location.getName())
			.x(location.getX())
			.y(location.getY())
			.address(location.getAddress())
			.roadAddress(location.getRoadAddress())
			.url(location.getUrl())
			.phone(location.getPhone())
			.build();
	}
}
