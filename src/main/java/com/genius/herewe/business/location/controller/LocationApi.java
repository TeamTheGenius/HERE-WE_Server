package com.genius.herewe.business.location.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.genius.herewe.business.location.LocationRequest;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.ExceptionResponse;
import com.genius.herewe.core.global.response.SlicingResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

public interface LocationApi {
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

	@Operation(summary = "장소 등록", description = "검색한 장소를 등록합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "장소 등록 성공"
		),
		@ApiResponse(
			responseCode = "404",
			description = "momentId(식별자)를 통해 모먼트 엔티티를 찾지 못했을 때",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "momentId(식별자)를 통해 모먼트 엔티티를 찾지 못했을 때",
						value = """
							{
								"resultCode": "404",
								"code": "MOMENT_NOT_FOUND",
								"message": "해당 MOMENT를 찾을 수 없습니다."
							}
							"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "409",
			description = """
				1. 동시성 문제로 인해 예외 발생한 경우
				2. 해당 인덱스로 이미 장소가 등록되어 있는 경우
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "1. 동시성 문제로 인해 예외 발생한 경우",
						value = """
							{
								"resultCode": "409",
								"code": "CONCURRENT_MODIFICATION_EXCEPTION",
								"message": "이 데이터는 다른 사용자에 의해 수정되었습니다. 다시 시도해주세요."
							}
							"""
					),
					@ExampleObject(
						name = "2. 해당 인덱스로 이미 장소가 등록되어 있는 경우",
						value = """
							{
								"resultCode": "409",
								"code": "LOCATION_ALREADY_EXISTS",
								"message": "해당 순서의 장소가 이미 추가되었습니다. 잠시 후 다시 시도해주세요."
							}
							"""
					)
				}
			)
		)
	})
	CommonResponse addPlace(@PathVariable Long momentId, @Valid @RequestBody LocationRequest locationRequest);
}
