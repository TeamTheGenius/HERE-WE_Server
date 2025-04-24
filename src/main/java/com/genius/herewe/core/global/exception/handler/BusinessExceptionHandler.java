package com.genius.herewe.core.global.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.genius.herewe.core.global.exception.BusinessException;
import com.genius.herewe.core.global.response.ExceptionResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BusinessExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
		log.error("[BUSINESS EXCEPTION] {}", e.getMessage(), e);

		return ResponseEntity.status(e.getStatus()).body(
			new ExceptionResponse(e.getErrorCode())
		);
	}
}
