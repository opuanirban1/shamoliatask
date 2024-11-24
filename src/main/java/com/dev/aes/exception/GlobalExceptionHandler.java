package com.dev.aes.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;
import java.util.Set;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleSecurityException(Exception exception) {

        exception.printStackTrace();

        if (exception instanceof BadCredentialsException) {
             return new ResponseEntity<>(
                    new OcrDmsError("The username or password is incorrect"),
                     HttpStatusCode.valueOf(401)
            );
        }

        if (exception instanceof AccountStatusException) {
            return new ResponseEntity<>(
                    new OcrDmsError("The account is locked"),
                    HttpStatusCode.valueOf(403)
            );
        }

        if (exception instanceof AccessDeniedException) {
            return new ResponseEntity<>(
                    new OcrDmsError("You are not authorized to access this resource"),
                    HttpStatusCode.valueOf(403)
            );
        }

        if (exception instanceof SignatureException) {
            return new ResponseEntity<>(
                    new OcrDmsError("The JWT signature is invalid"),
                    HttpStatusCode.valueOf(403)
            );
        }

        if (exception instanceof ExpiredJwtException) {
            return new ResponseEntity<>(
                    new OcrDmsError("The JWT token has expired"),
                    HttpStatusCode.valueOf(403)
            );
        }


        return new ResponseEntity<>(
                new OcrDmsError("Unknown internal server error."),
                HttpStatusCode.valueOf(500)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object>
    handleConstraintViolationExceptionAllException(ConstraintViolationException ex, WebRequest request) {
        ApiError apiError = new ApiError();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
        violations.forEach(violation -> {
            OcrDmsError ocrDmsError = getErrorByMessage(violation.getMessageTemplate());
            apiError.addError(ocrDmsError);
        });
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    public static OcrDmsError getErrorByMessage(String message) {
        OcrDmsError error = new OcrDmsError();
        error.setMessage(message);
        return error;
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
//        return new ResponseEntity<>(Objects.requireNonNull(ex.getDetailMessageArguments())[1], status);
        return new ResponseEntity<>(new OcrDmsError(Objects.requireNonNull(ex.getDetailMessageArguments())[1].toString()), status);
    }

    @ExceptionHandler(value = {OcrDmsException.class})
    public ResponseEntity<Object> handleAesExceptions(OcrDmsException exception) {
        return new ResponseEntity<>(
                new OcrDmsError(exception.getMessage()),
                exception.getHttpStatus()
        );
    }
}
