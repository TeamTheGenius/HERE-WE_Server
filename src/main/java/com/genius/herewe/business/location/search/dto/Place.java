package com.genius.herewe.business.location.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "Map API 결과를 담을 객체")
public record Place(
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
}
