package com.genius.herewe.util.response;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ListResponse<T> extends CommonResponse {
    private List<T> dataList;
    private int count;

    public ListResponse(HttpStatus status, List<T> dataList) {
        super(status);
        this.dataList = dataList;
        this.count = dataList.size();
    }
}
