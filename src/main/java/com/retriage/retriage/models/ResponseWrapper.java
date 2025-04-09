package com.retriage.retriage.models;

import lombok.Data;

@Data
public class ResponseWrapper<T> {
    private int httpStatus;
    private String error;
    private T data;

    public ResponseWrapper(int httpStatus, String message, T data) {
        this.httpStatus = httpStatus;
        this.error = message;
        this.data = data;
    }
}
