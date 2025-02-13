package com.genius.herewe.infra.file.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.genius.herewe.infra.file.dto.FileResponse;
import com.genius.herewe.core.global.response.CommonResponse;
import com.genius.herewe.core.global.response.ExceptionResponse;
import com.genius.herewe.core.global.response.SingleResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Files API", description = "파일 관련 API")
public interface FilesApi {

	@Operation(
		summary = "파일 조회",
		description = "사용자의 프로필 사진/크루 썸네일/모먼트 썸네일 조회"
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "조회 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = "type에 profile/crew/moment 중 하나가 아닌 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
					value = """
						{
						    "resultCode": 400,
						    "code": "NOT_SUPPORTED_IMAGE_TYPE",
						    "message": "파일을 받을 수 있는 종류가 아닙니다. profile, crew, moment 중 하나를 입력해주세요."
						}
						"""
				)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "id(PK)를 통해 엔티티를 찾지 못한 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
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
	@GetMapping("/{id}")
	SingleResponse<FileResponse> getFile(
		@Parameter(description = "찾고자하는 객체의 식별자(PK)") @PathVariable Long id,
		@Parameter(description = "조회하고자 하는 파일의 종류(profile, crew, moment 중 택 1)") @RequestParam("type") String type
	);

	@Operation(
		summary = "파일 등록",
		description = "사용자의 프로필 사진/크루 썸네일/모먼트 썸네일 파일 저장")
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "파일 등록 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = """
				1. type에 profile/crew/moment 중 하나가 아닌 경우
				2. 파일 저장에 실패한 경우
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						value = """
							{
							    "resultCode": 400,
							    "code": "NOT_SUPPORTED_IMAGE_TYPE",
							    "message": "파일을 받을 수 있는 종류가 아닙니다. profile, crew, moment 중 하나를 입력해주세요."
							}
							"""
					),
					@ExampleObject(
						value = """
							{
								"resultCode": 400,
								"code": "FILE_NOT_SAVED",
								"message": "파일이 정상적으로 저장되지 않았습니다."
							}
							"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "id(PK)를 통해 엔티티를 찾지 못한 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
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
	@PostMapping("/{id}")
	SingleResponse<FileResponse> uploadFile(@PathVariable Long id,
		@Parameter(description = "조회하고자 하는 파일의 종류(profile, crew, moment 중 택 1)") @RequestParam("type") String type,
		@Parameter(description = "저장하고자하는 파일을 form-data의 files 항목으로 전달") @RequestParam(value = "files", required = false) MultipartFile multipartFile
	);

	@Operation(summary = "파일 갱신", description = "사용자의 프로필/크루 썸네일/모먼트 썸네일 갱신")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "파일 갱신 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = """
				1. type이 profile/crew/moment 중 하나가 아닌 경우
				2. 기존에 존재하던 파일의 삭제가 정상적으로 이루어지지 않은 경우
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						value = """
							{
								"resultCode": 400,
								"code": "NOT_SUPPORTED_IMAGE_TYPE",
								"message": "파일을 받을 수 있는 종류가 아닙니다. profile, crew, moment 중 하나를 입력해주세요."
							}
							"""
					),
					@ExampleObject(
						value = """
							{
								"resultCode": 400,
								"code": "FILE_NOT_DELETED",
								"message": "파일이 정상적으로 삭제되지 않았습니다."
							}
							"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "id(PK)를 통해 엔티티를 찾지 못한 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
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
	@PatchMapping("/{id}")
	SingleResponse<FileResponse> updateFile(
		@Parameter(description = "찾고자하는 객체의 식별자(PK)") @PathVariable Long id,
		@Parameter(description = "조회하고자 하는 파일의 종류(profile, crew, moment 중 택 1)") @RequestParam("type") String type,
		@Parameter(description = "저장하고자하는 파일을 form-data의 files 항목으로 전달") @RequestParam(value = "files", required = false) MultipartFile multipartFile
	);

	@Operation(summary = "파일 삭제", description = "사용자의 프로필, 크루 썸네일, 모먼트 썸네일 삭제")
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "파일 삭제 성공"
		),
		@ApiResponse(
			responseCode = "400",
			description = """
				1. type이 profile/crew/moment 중 하나가 아닌 경우
				2. 기존에 존재하던 파일의 삭제가 정상적으로 이루어지지 않은 경우
				""",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = {
					@ExampleObject(
						value = """
							{
								"resultCode": 400,
								"code": "NOT_SUPPORTED_IMAGE_TYPE",
								"message": "파일을 받을 수 있는 종류가 아닙니다. profile, crew, moment 중 하나를 입력해주세요."
							}
							"""
					),
					@ExampleObject(
						value = """
							{
								"resultCode": 400,
								"code": "FILE_NOT_DELETED",
								"message": "파일이 정상적으로 삭제되지 않았습니다."
							}
							"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "id(PK)를 통해 엔티티를 찾지 못한 경우",
			content = @Content(
				schema = @Schema(implementation = ExceptionResponse.class),
				examples = @ExampleObject(
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
	@DeleteMapping("/{id}")
	public CommonResponse deleteFile(
		@Parameter(description = "찾고자하는 객체의 식별자(PK)") @PathVariable Long id,
		@Parameter(description = "조회하고자 하는 파일의 종류(profile, crew, moment 중 택 1)") @RequestParam("type") String type
	);
}
