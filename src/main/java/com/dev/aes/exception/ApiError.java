package com.dev.aes.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ApiError {
    List<OcrDmsError> apiErrors;

    public void addError(OcrDmsError error) {
        if (apiErrors == null) {
            apiErrors = new ArrayList<>();
        }
        apiErrors.add(error);
    }
}