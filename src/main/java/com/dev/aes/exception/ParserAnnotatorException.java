package com.dev.aes.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class ParserAnnotatorException extends RuntimeException{

    @Getter
    private final HttpStatus httpStatus;

    public ParserAnnotatorException(String message){
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public ParserAnnotatorException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}