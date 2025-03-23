package com.genius.herewe.business.moment.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.genius.herewe.business.moment.dto.MomentRequest;
import com.genius.herewe.business.moment.dto.MomentResponse;
import com.genius.herewe.core.global.response.ExceptionResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.annotation.HereWeUser;
import com.genius.herewe.core.user.domain.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface MomentApi {
	@Operation(summary = "모먼트 생성", description = "특정 크루에 모먼트 생성")
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "모먼트 생성 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = """
				1. 필수 입력 항목 누락 시
				2. 크루 참여 인원이 2명 미만일 때
				3. 참여 일자(meetAt) & 마감일자(closedAt)이 요청 일자 이전일 때 / 참여 일자가 마감 일자 이전일 때
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "필수 입력 항목(크루 생성의 경우 모든 항목) 누락 시",
						value = """
							{
								"resultCode": "400",
								"code": "REQUIRED_FIELD_MISSING",
								"message": "해당 항목은 필수 항목입니다. 입력 값을 확인해주세요."
							}
							"""
					),
					@ExampleObject(
						name = "크루 참여 인원이 2명 미만일 때",
						value = """
							{
								"resultCode": "400",
								"code": "INVALID_MOMENT_CAPACITY",
								"message": "MOMENT의 최대 참여 가능 인원은 2명 이상이어야 합니다."
							}
							"""
					),
					@ExampleObject(
						name = "참여 일자(meetAt) & 마감일자(closedAt)이 요청 일자 이전일 때 / 참여 일자가 마감 일자 이전일 때",
						value = """
							{
								"resultCode": "400",
								"code": "INVALID_MOMENT_DATE",
								"message": "만남일자(meetAt)/마감일자(closedAt)는 오늘보다 나중이어야 하며, 만남일자가 마감일자보다 더 이후여야 합니다."
							}
							"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = """
				1. userId를 통해 User 엔티티를 찾을 수 없을 때
				2. crewId를 통해 Crew 엔티티를 찾을 수 없을 때
				""",
			content = @Content(
				examples = {
					@ExampleObject(
						name = "userId를 통해 User 엔티티를 찾을 수 없을 때",
						value = """
							{
								"resultCode": "404",
								"code": "MEMBER_NOT_FOUND",
								"message": "사용자를 찾을 수 없습니다."
							}
							"""
					),
					@ExampleObject(
						name = "crewId를 통해 Crew 엔티티를 찾을 수 없을 때",
						value = """
							{
								"resultCode": "404",
								"code": "CREW_NOT_FOUND",
								"message": "해당 CREW를 찾을 수 없습니다."
							}
							"""
					)
				}
			)
		)
	})
	SingleResponse<MomentResponse> createMoment(@HereWeUser User user, @RequestParam(name = "crewId") Long crewId,
		@RequestBody MomentRequest momentRequest);

	@Operation(summary = "모먼트 수정", description = "모먼트의 정보 수정")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "모먼트 수정 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = """
				1. 크루 참여 인원이 2명 미만일 때
				2. 참여 일자(meetAt) & 마감일자(closedAt)이 요청 일자 이전일 때 / 참여 일자가 마감 일자 이전일 때
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "크루 참여 인원이 2명 미만일 때",
						value = """
							{
								"resultCode": "400",
								"code": "INVALID_MOMENT_CAPACITY",
								"message": "MOMENT의 최대 참여 가능 인원은 2명 이상이어야 합니다."
							}
							"""
					),
					@ExampleObject(
						name = "참여 일자(meetAt) & 마감일자(closedAt)이 요청 일자 이전일 때 / 참여 일자가 마감 일자 이전일 때",
						value = """
							{
								"resultCode": "400",
								"code": "INVALID_MOMENT_DATE",
								"message": "만남일자(meetAt)/마감일자(closedAt)는 오늘보다 나중이어야 하며, 만남일자가 마감일자보다 더 이후여야 합니다."
							}
							"""
					)
				}
			)
		)
	})
	SingleResponse<MomentResponse> modifyMoment(@PathVariable Long momentId, @RequestBody MomentRequest momentRequest);
}
