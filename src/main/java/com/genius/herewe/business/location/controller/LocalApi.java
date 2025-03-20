package com.genius.herewe.business.location.controller;

import org.springframework.web.bind.annotation.RequestParam;

import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.core.global.response.SlicingResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface LocalApi {
	@Operation(summary = "키워드를 통한 장소 검색", description = "Kakao Map API를 이용하여 키워드 검색 결과를 반환합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "키워드를 이용한 장소 검색 완료"
		),
	})
	SlicingResponse<Place> search(@RequestParam("keyword") String keyword,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size);
}
