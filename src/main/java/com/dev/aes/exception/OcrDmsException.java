package com.dev.aes.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class OcrDmsException extends RuntimeException{

    @Getter
    private final HttpStatus httpStatus;

    public OcrDmsException(String message){
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public OcrDmsException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}