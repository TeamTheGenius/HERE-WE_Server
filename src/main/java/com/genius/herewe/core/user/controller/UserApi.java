package com.genius.herewe.core.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.ExceptionResponse;
import com.genius.herewe.core.global.response.SingleResponse;
import com.genius.herewe.core.user.dto.SignupRequest;
import com.genius.herewe.core.user.dto.SignupResponse;
import com.genius.herewe.infra.file.dto.FileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User API", description = "사용자 관련 API")
public interface UserApi {
	@Operation(summary = "닉네임 중복 확인", description = "회원가입 과정에서 사용하고자하는 닉네임이 중복인지 확인")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "닉네임 중복 없음. 사용 가능."
		),
		@ApiResponse(
			responseCode = "400",
			description = "해당 닉네임이 이미 존재하는 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
					name = "해당 닉네임이 이미 존재하는 경우",
					value = """
						{
							"resultCode": "400",
							"code": "NICKNAME_DUPLICATED",
							"message": "이미 존재하는 닉네임입니다. 닉네임은 중복될 수 없습니다."
						}
						"""
				)
			)
		)
	})
	@GetMapping("/auth/check-nickname")
	CommonResponse checkNicknameDuplicated(
		@Parameter(description = "중복 확인을 해볼 닉네임") @RequestParam(value = "nickname") String nickname);

	@Operation(summary = "회원가입 요청", description = "사용자 정보를 통해 회원가입 요청")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "회원가입 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = """
				1. 이미 가입이 되어 있는 사용자인 경우
				2. 사용하고자 하는 닉네임이 이미 존재하는 경우
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						name = "1. 이미 가입이 되어 있는 사용자인 경우",
						value = """
							{
								"resultCode": 400,
								"code": "ALREADY_REGISTERED",
								"message": "이미 가입한 사용자입니다."
							}
							"""
					),
					@ExampleObject(
						name = "2. 사용하고자하는 닉네임이 이미 존재하는 경우",
						value = """
							{
								"resultCode": 400,
								"code": "NICKNAME_DUPLICATED",
								"message": "이미 존재하는 닉네임입니다. 닉네임은 중복될 수 없습니다."
							}
							"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "RequestBody로 전달받은 userId를 통해 사용자를 찾지 못한 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
					name = "RequestBody로 전달받은 userId를 통해 사용자를 찾지 못한 경우",
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
	@PostMapping("/auth/signup")
	SingleResponse<SignupResponse> signup(
		@Parameter(description = "회원가입에 필요한 정보들 - 사용자 식별자 & 닉네임") @RequestBody SignupRequest signupRequest);

	@Operation(summary = "사용자 프로필 조회 (회원가입 단계)", description = "회원가입 단계에서 사용자의 프로필 조회")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "사용자 프로필 조회 성공"
		),
		@ApiResponse(
			responseCode = "404",
			description = "사용자를 찾을 수 없는 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
					name = "사용자를 찾을 수 없는 경우",
					value = """
						{
							"resultCode": 404,
							"code": "MEMBER_NOT_FOUND",
							"message": "사용자를 찾을 수 없습니다."
						}
						"""
				)
			)
		)
	})
	@GetMapping("/auth/profile/{id}")
	SingleResponse<FileResponse> getProfile(
		@Parameter(description = "사용자의 회원가입 토큰") @RequestParam("token") String token);

	@Operation(summary = "사용자 프로필 갱신", description = "회원가입 과정에서 사용자의 프로필 갱신")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "프로필 사진 갱신 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "기존에 존재하던 파일의 삭제가 정상적으로 이루어지지 않은 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
					name = "파일 삭제가 정상적으로 이루어지지 않은 경우",
					value = """
						{
							"resultCode": 400,
							"code": "FILE_NOT_DELETED",
							"message": "파일이 정상적으로 삭제되지 않았습니다."
						}
						"""
				)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "사용자를 찾지 못한 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
					name = "사용자를 찾지 못한 경우",
					value = """
						{
							"resultCode": 404,
							"code": "MEMBER_NOT_FOUND",
							"message": "사용자를 찾을 수 없습니다."
						}
						"""
				)
			)
		)
	})
	@PatchMapping("/auth/profile/{id}")
	SingleResponse<FileResponse> updateProfile(
		@Parameter(description = "사용자의 회원가입 토큰") @RequestParam("token") String token,
		@Parameter(description = "저장하고자하는 파일을 form-data의 files 항목으로 전달") @RequestParam(value = "files") MultipartFile multipartFile
	);
}
