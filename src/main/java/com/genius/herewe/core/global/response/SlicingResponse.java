package com.genius.herewe.core.global.response;

import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "슬라이스(무한 스크롤)형태의 응답 형식")
public class SlicingResponse<T> extends CommonResponse {
	@Schema(description = "무한 스크롤 형식의 데이터")
	private Slice<T> data;

	public SlicingResponse(HttpStatus status, Slice<T> data) {
		super(status);
		this.data = data;
	}
}
