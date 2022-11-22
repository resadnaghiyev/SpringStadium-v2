package com.rashad.loginwithsocial.exception;

import com.rashad.loginwithsocial.model.CustomResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

//@ControllerAdvice (you have to use ResponseBody when using this)
@RestControllerAdvice
public class MyControllerAdvice {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", exception.getMessage());
        return new ResponseEntity<>(new CustomResponse(
                false, null, "", errorMap), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResponseEntity<CustomResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("method", exception.getMessage());
        return new ResponseEntity<>(new CustomResponse(
                false, null, "", errorMap), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(
                error -> errorMap.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(new CustomResponse(
                false, null, "", errorMap), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomResponse> handleMultipartException(MultipartException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("multipart", exception.getMessage());
        return new ResponseEntity<>(new CustomResponse(
                false, null, "", errorMap), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> handleSql(SQLIntegrityConstraintViolationException exception) {
        Map<String, String> errorMap = new HashMap<>();
        String exMess = exception.getMessage();
        String error = exMess.substring(0, exMess.indexOf("for") - 1);
        errorMap.put("sql", error);
        return new ResponseEntity<>(new CustomResponse(
                false, null, "", errorMap), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleEmptyBody(HttpMessageNotReadableException exception) {
        Map<String, String> errorMap = new HashMap<>();
        String exMess = exception.getMessage();
        String error = exMess != null ? exMess.substring(0, exMess.indexOf(":")) : null;
        errorMap.put("body", error);
        return new ResponseEntity<>(new CustomResponse(
                false, null, "", errorMap), HttpStatus.BAD_REQUEST);
    }
}


//        ErrorObject errorObject = new ErrorObject();
//        errorObject.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//        errorObject.setMessage(exception.getMessage());
//        errorObject.setTimestamp(System.currentTimeMillis());