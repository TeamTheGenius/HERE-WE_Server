package com.genius.herewe.core.global.response;

import java.util.List;

import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "리스트 형태의 응답 형식")
public class ListResponse<T> extends CommonResponse {
	@Schema(description = "데이터 목록")
	private List<T> dataList;
	@Schema(description = "데이터 개수", example = "5")
	private int count;

	public ListResponse(HttpStatus status, List<T> dataList) {
		super(status);
		this.dataList = dataList;
		this.count = dataList.size();
	}
}
