package com.genius.herewe.util.exception;

import com.genius.herewe.util.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BusinessExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException e) {
        log.error("[BUSINESS EXCEPTION] {}", e.getMessage(), e);

        return ResponseEntity.status(e.getStatus()).body(
                new ExceptionResponse(e.getStatus(), e.getMessage())
        );
    }
}
