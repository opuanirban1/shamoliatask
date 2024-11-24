package com.dev.aes.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OcrDmsError {
    private String message;

    public OcrDmsError(String message) {
        this.message = message;
    }
}