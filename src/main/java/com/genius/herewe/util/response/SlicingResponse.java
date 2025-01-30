package com.genius.herewe.util.response;

import lombok.Getter;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;

@Getter
public class SlicingResponse<T> extends CommonResponse {
    private Slice<T> data;

    public SlicingResponse(HttpStatus status, Slice<T> data) {
        super(status);
        this.data = data;
    }
}
