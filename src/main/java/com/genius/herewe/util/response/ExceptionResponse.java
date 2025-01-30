package com.genius.herewe.util.response;

import org.springframework.http.HttpStatus;

public class ExceptionResponse extends CommonResponse {
    private String cause;

    public ExceptionResponse(HttpStatus status, String cause) {
        super(status);
        this.cause = cause;
    }
}
