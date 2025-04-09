package com.genius.herewe.core.global.response;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "기본 응답 형식")
public class CommonResponse {
	@Schema(description = "응답 코드", example = "200")
	private int resultCode;
	@Schema(description = "HttpsStatus 코드", example = "OK")
	private String code;

	public CommonResponse(HttpStatus code) {
		this.code = code.name();
		this.resultCode = code.value();
	}

	public static CommonResponse ok() {
		return new CommonResponse(HttpStatus.OK);
	}

	public static CommonResponse created() {
		return new CommonResponse(HttpStatus.CREATED);
	}
}