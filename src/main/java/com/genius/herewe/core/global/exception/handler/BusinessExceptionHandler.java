package com.genius.herewe.core.global.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.global.response.ExceptionResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class BusinessExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
		log.error("[BUSINESS EXCEPTION] {}", e.getMessage(), e);

		return ResponseEntity.status(e.getStatus()).body(
			new ExceptionResponse(e.getErrorCode())
		);
	}
}
