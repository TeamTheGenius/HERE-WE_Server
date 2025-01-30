package com.genius.herewe.util.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommonResponse {
    private HttpStatus code;
    private int resultCode;

    public CommonResponse(HttpStatus code) {
        this.code = code;
        this.resultCode = code.value();
    }
}