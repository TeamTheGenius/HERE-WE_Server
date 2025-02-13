package com.genius.herewe.core.global.response;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "페이징 형태의 응답 형식")
public class PagingResponse<T> extends CommonResponse {
	@Schema(description = "페이징 형식의 데이터")
	private Page<T> data;

	public PagingResponse(HttpStatus status, Page<T> data) {
		super(status);
		this.data = data;
	}
}
