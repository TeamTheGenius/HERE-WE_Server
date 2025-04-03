package com.genius.herewe.business.location.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.genius.herewe.business.location.LocationRequest;
import com.genius.herewe.business.location.dto.PlaceResponse;
import com.genius.herewe.business.location.search.dto.Place;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.ExceptionResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.global.response.SlicingResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Location API", description = "장소 관련 API")
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

	@Operation(summary = "장소 목록 조회", description = "특정 모먼트에 등록된 장소 리스트 조회")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "장소 정보 조회 성공"
		)
	})
	SingleResponse<PlaceResponse> inquiryPlaces(@PathVariable Long momentId);

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

	@Operation(summary = "등록되어 있는 장소 삭제", description = "모먼트에 등록되어 있는 장소 삭제")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "장소 삭제 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = """
				1. query string으로 전달한 인덱스(삭제할 인덱스)가 유효하지 않을 때
				2. 아직 예외 처리하지 못한 에러일 때 - 별도 처리 필요
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "1. query string으로 전달한 인덱스(삭제할 인덱스)가 유효하지 않을 때",
						value = """
							{
								"resultCode": "400",
								"code": "INVALID_LOCATION_INDEX",
								"message": "유효하지 않은 위치 인덱스입니다."
							}
							"""
					),
					@ExampleObject(
						name = "2. 아직 예외 처리하지 못한 에러일 때 - 별도 처리 필요",
						value = """
							{
								"resultCode": "400",
								"code": "UNEXPECTED_ERROR",
								"message": "처리되지 않은 에러입니다. 추가 처리가 필요합니다."
							}
							"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "사용자가 참여하지 않은 모먼트에서 장소 삭제 요청 시",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "사용자가 참여하지 않은 모먼트에서 장소 삭제 요청 시 (모먼트 참여 정보를 찾을 수 없을 때)",
						value = """
							{
								"resultCode": "404",
								"code": "MOMENT_PARTICIPATION_NOT_FOUND",
								"message": "모먼트 참여 정보를 찾을 수 없습니다."
							}
							"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "409",
			description = "동시성 문제로 인해 요청이 처리되지 못했을 때 - 재시도 필요",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "동시성 문제로 인해 요청이 처리되지 못했을 때",
						value = """
							{
								"resultCode": "409",
								"code": "CONCURRENT_MODIFICATION_EXCEPTION",
								"message": "이 데이터는 다른 사용자에 의해 수정되었습니다. 다시 시도해주세요."
							}
							"""
					)
				}
			)
		)
	})
	CommonResponse deletePlace(@HereWeUser User user,
							   @PathVariable Long momentId,
							   @RequestParam("index") int locationIndex);
}
