package com.genius.herewe.core.global.exception;

import com.genius.herewe.core.global.response.ExceptionResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ExceptionResponse> handleGlobalException(Exception e) {
		log.error("[UNHANDLED ERROR] {}", e.getMessage(), e);

		return ResponseEntity.badRequest().body(
			new ExceptionResponse(HttpStatus.BAD_REQUEST, e.getMessage())
		);
	}
}
