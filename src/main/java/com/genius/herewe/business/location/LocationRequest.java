package com.genius.herewe.business.location;

import com.genius.herewe.business.location.search.dto.Place;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "추가 할 장소 DTO")
public record LocationRequest(
	@Schema(description = "장소 정보")
	Place place
) {
}
