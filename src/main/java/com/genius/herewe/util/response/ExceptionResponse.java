package com.genius.herewe.util.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonPropertyOrder({"resultCode", "code", "message"})
public class ExceptionResponse extends CommonResponse {
    private String message;

    public ExceptionResponse(HttpStatus status, String message) {
        super(status);
        this.message = message;
    }
}
