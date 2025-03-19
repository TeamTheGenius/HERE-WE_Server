package com.genius.herewe.business.location.search.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

@Builder
public record Place(
	@JsonProperty(value = "place_name")
	String name,
	@JsonProperty(value = "address_name")
	String address,
	@JsonProperty(value = "road_address_name")
	String roadAddress,
	@JsonProperty(value = "x")
	Double x,
	@JsonProperty(value = "y")
	Double y,
	@JsonProperty(value = "phone")
	String phone
) {
}
