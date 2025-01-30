package com.genius.herewe.util.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class SingleResponse<T> extends CommonResponse {
    private T data;

    public SingleResponse(HttpStatus status, T data) {
        super(status);
        this.data = data;
    }
}
