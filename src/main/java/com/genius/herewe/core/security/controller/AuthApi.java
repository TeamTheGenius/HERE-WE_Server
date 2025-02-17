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
import jakarta.servlet.http.HttpServletResponse;

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
}
