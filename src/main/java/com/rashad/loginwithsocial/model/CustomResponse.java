package com.rashad.loginwithsocial.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomResponse {

    private Boolean success;
    private Object data;
    private String message;
    private Object error;
}
