package com.genius.herewe.core.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private HttpStatus status;
	private ErrorCode errorCode;

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.status = errorCode.getStatus();
	}

	public BusinessException(ErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
		this.status = errorCode.getStatus();
	}

	public BusinessException(Throwable cause) {
		super(cause);
	}
}
