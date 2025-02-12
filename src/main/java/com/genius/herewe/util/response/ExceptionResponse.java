package com.genius.herewe.util.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonPropertyOrder({"resultCode", "code", "message"})
@Schema(description = "예외 발생 시 응답 형식")
public class ExceptionResponse extends CommonResponse {
	@Schema(description = "예외 메세지", example = "사용자를 찾을 수 없습니다.")
	private String message;

	public ExceptionResponse(HttpStatus status, String message) {
		super(status);
		this.message = message;
	}
}
