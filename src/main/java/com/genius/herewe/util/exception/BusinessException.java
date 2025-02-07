package com.genius.herewe.util.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private HttpStatus status;

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.status = errorCode.getStatus();
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}
}
