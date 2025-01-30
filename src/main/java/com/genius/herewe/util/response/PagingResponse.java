package com.genius.herewe.util.response;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Getter
public class PagingResponse<T> extends CommonResponse {
    private Page<T> data;

    public PagingResponse(HttpStatus status, Page<T> data) {
        super(status);
        this.data = data;
    }
}
