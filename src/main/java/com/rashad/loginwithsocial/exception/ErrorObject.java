package com.rashad.loginwithsocial.exception;

import lombok.Data;

@Data
public class ErrorObject {

    private Integer statusCode;

    private String message;

    private String path;

    private long timestamp;

}
