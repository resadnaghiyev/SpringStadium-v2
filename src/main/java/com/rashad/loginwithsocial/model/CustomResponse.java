package com.rashad.loginwithsocial.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomResponse {

    private Boolean success = true;
    private Object data;
    private String message;
    private Object error;

    public CustomResponse(Object data) {
        this.data = data;
    }

    public CustomResponse(String message) {
        this.message = message;
    }

    public CustomResponse(boolean success, Object error) {
        this.success = success;
        this.error = error;
    }

    public CustomResponse(Object data, String message) {
        this.data = data;
        this.message = message;
    }
}
