package com.genius.herewe.util.response;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
	private int resultCode;
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