package com.dev.aes.payloads.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ForgotPasswordDTO {

    @Pattern(regexp = "[a-zA-Z0-9.]*[@][a-zA-Z]+\\.(com|net|org)", message = "email id is invalid")
    private String email;
}