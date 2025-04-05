package com.genius.herewe.core.global.exception.handler;

import static com.genius.herewe.core.global.exception.ErrorCode.*;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.genius.herewe.core.global.response.ExceptionResponse;

import lombok.extern.slf4j.Slf4j;

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

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
		// 유효성 검사 실패 정보 추출
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError)error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		return ResponseEntity.badRequest().body(
			new ExceptionResponse(VALIDATION_FAILED)
		);
	}
}
