package com.genius.herewe.core.global.exception.handler;

import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.genius.herewe.core.global.exception.ErrorCode;
import com.genius.herewe.core.global.response.ExceptionResponse;

import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ConcurrencyExceptionHandler {

	@ExceptionHandler({
		DataIntegrityViolationException.class,
		PessimisticLockingFailureException.class,
		CannotAcquireLockException.class,
		UnexpectedRollbackException.class,
		OptimisticLockException.class,
		DataAccessException.class
	})
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<ExceptionResponse> handleConcurrencyException(Exception ex) {
		log.warn("동시성 충돌 발생: {}", ex.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT)
			.body(new ExceptionResponse(ErrorCode.CONCURRENT_MODIFICATION_EXCEPTION));
	}
}