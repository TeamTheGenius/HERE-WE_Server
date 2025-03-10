package com.genius.herewe.business.crew.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.genius.herewe.business.crew.dto.CrewCreateRequest;
import com.genius.herewe.business.crew.dto.CrewMemberResponse;
import com.genius.herewe.business.crew.dto.CrewModifyRequest;
import com.genius.herewe.business.crew.dto.CrewPreviewResponse;
import com.genius.herewe.business.crew.dto.CrewResponse;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.ExceptionResponse;
import com.genius.herewe.core.global.response.PagingResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

public interface CrewApi {
	@Operation(summary = "크루 정보 조회", description = "크루 ID, 이름, 리더 이름, 역할, 소개글, 참여인원 정보 반환")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "크루의 정보 정상적으로 조회"
		),
		@ApiResponse(
			responseCode = "404",
			description = """
				1. 크루 정보를 찾을 수 없는 경우
				2. 크루 참여 정보를 찾을 수 없는 경우
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "크루 정보를 찾을 수 없는 경우",
						value = """
							{
								"resultCode": "404",
								"code": "CREW_NOT_FOUND",
								"message": "해당 CREW를 찾을 수 없습니다."
							}
							"""
					),
					@ExampleObject(
						name = "크루 참여 정보를 찾을 수 없는 경우",
						value = """
							{
								"resultCode": "404",
								"code": "CREW_JOIN_INFO_NOT_FOUND",
								"message": "해당 크루에 대한 참여 정보가 없습니다."
							}
							"""
					)
				}
			)
		)
	})
	SingleResponse<CrewResponse> inquiryCrewInfo(@HereWeUser User user, @PathVariable Long crewId);

	@Operation(summary = "크루 정보 조회", description = "크루 ID, 이름, 리더 이름, 역할, 소개글, 참여인원 정보 반환")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = """
				크루원들의 정보 pagination 형태로 정상적으로 조회 완료
				참고) page의 size는 최대 20으로 제한
				"""
		)
	})
	PagingResponse<CrewMemberResponse> getMemberInfo(
		@PathVariable Long crewId,
		@PageableDefault(size = 10, page = 0) Pageable pageable);

	@Operation(summary = "크루 생성", description = "이름/소개글 정보를 받아 크루 생성. (이름: 필수 정보)")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "크루 정상적으로 생성 완료"
		),
		@ApiResponse(
			responseCode = "404",
			description = "사용자 정보를 찾을 수 없는 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
					name = "사용자 정보를 찾을 수 없는 경우",
					value = """
						{
							"resultCode": "404",
							"code": "MEMBER_NOT_FOUND",
							"message": "사용자를 찾을 수 없습니다."
						}
						"""
				)
			)

		)
	})
	SingleResponse<CrewPreviewResponse> createCrew(
		@HereWeUser User user,
		@Valid @RequestBody CrewCreateRequest crewCreateRequest);

	@Operation(summary = "크루 수정", description = "크루 이름, 소개글 수정")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "크루의 정보(이름, 소개글) 정상적으로 수정 완료"
		),
		@ApiResponse(
			responseCode = "403",
			description = "크루 리더가 아닌 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "크루 리더가 아니라 권한이 없는 경우",
						value = """
							{
								"resultCode": "403",
								"code": "LEADER_PERMISSION_DENIED",
								"message": "CREW LEADER의 권한이 필요합니다."
							}
							"""
					)
				}
			)
		)
		,
		@ApiResponse(
			responseCode = "404",
			description = """
				1. 크루 정보를 찾을 수 없는 경우
				2. 크루 참여 정보를 찾을 수 없는 경우
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "크루 정보를 찾을 수 없는 경우",
						value = """
							{
								"resultCode": "404",
								"code": "CREW_NOT_FOUND",
								"message": "해당 CREW를 찾을 수 없습니다."
							}
							"""
					),
					@ExampleObject(
						name = "크루 참여 정보를 찾을 수 없는 경우",
						value = """
							{
								"resultCode": "404",
								"code": "CREW_JOIN_INFO_NOT_FOUND",
								"message": "해당 크루에 대한 참여 정보가 없습니다."
							}
							"""
					)
				}
			)
		)
	})
	SingleResponse<CrewPreviewResponse> modifyCrew(
		@HereWeUser User user,
		@PathVariable Long crewId,
		@RequestBody CrewModifyRequest crewModifyRequest);

	@Operation(summary = "크루 참여 - 임시 API (추가 업데이트 예정)", description = "특정 사용자가 특정 크루에 참여할 때 사용")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "해당 크루에 참여 완료"
		)
	})
	CommonResponse joinCrew(@HereWeUser User user, @PathVariable Long crewId);
}

