package com.genius.herewe.core.security.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.genius.herewe.core.global.response.ExceptionResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.security.dto.AuthRequest;
import com.genius.herewe.core.security.dto.AuthResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Auth API", description = "인증/인가 관련 API")
public interface AuthApi {

	@Operation(summary = "사용자 인가 요청", description = "소셜 로그인 이후, 사용자 인가 요청")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "인가 성공"
		),
		@ApiResponse(
			responseCode = "404",
			description = "전달받은 userId를 통해 사용자를 찾지 못한 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
					name = "전달받은 userId를 통해 사용자를 찾지 못한 경우",
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
	@PostMapping("/auth")
	SingleResponse<AuthResponse> authorize(HttpServletResponse response,
										   @RequestBody AuthRequest authRequest);

	@Operation(summary = "JWT 재발급 API", description = "JWT 재발급 API, refresh-token이 유효하다면 JWT를 재발급합니다.")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "JWT 재발급 성공"
		),
		@ApiResponse(
			responseCode = "401",
			description = """
				1. refresh-token을 Cookie에서 찾을 수 없는 경우
				2. refresh-token 자체가 유효하지 않은 경우
				3. Redis에서 해당 사용자의 토큰을 찾을 수 없는 경우
				4. refresh-token이 redis에 저장되어 있던 토큰 정보와 일치하지 않는 경우
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "1. refresh-token을 Cookie에서 찾을 수 없는 경우",
						value = """
							{
								"resultCode": "401",
								"code": "JWT_NOT_FOUND_IN_COOKIE",
								"message": "Cookie에서 JWT를 찾을 수 없습니다."
							}
							"""
					),
					@ExampleObject(
						name = "2. refresh-token 자체가 유효하지 않은 경우",
						value = """
							{
								"resultCode": "401",
								"code": "JWT_NOT_VALID",
								"message": "JWT가 유효하지 않습니다."
							}
							"""
					),
					@ExampleObject(
						name = "3. Redis에서 해당 사용자의 토큰을 찾을 수 없는 경우",
						value = """
							{
								"resultCode": "401",
								"code": "TOKEN_NOT_FOUND_IN_REDIS",
								"message": "Redis에서 저장되어 있는 토큰을 찾을 수 없습니다."
							}
							"""
					),
					@ExampleObject(
						name = "4. refresh-token이 redis에 저장되어 있던 토큰 정보와 일치하지 않는 경우",
						value = """
							{
								"resultCode": "401",
								"code": "TOKEN_HIJACKED",
								"message": "토큰 탈취가 감지되었습니다. 다시 로그인해주세요."
							}
							"""
					)
				}
			)
		)
	})
	SingleResponse<AuthResponse> reissueToken(HttpServletRequest request, HttpServletResponse response);
}
