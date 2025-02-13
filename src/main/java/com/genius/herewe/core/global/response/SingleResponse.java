package com.genius.herewe.core.global.response;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "단일 응답 형식")
public class SingleResponse<T> extends CommonResponse {
	@Schema(description = "단일 응답 데이터")
	private T data;

	public SingleResponse(HttpStatus status, T data) {
		super(status);
		this.data = data;
	}
}
