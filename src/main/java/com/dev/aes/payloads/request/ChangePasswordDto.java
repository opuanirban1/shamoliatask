package com.dev.aes.payloads.request;

import lombok.Data;

@Data
public class ChangePasswordDto {
    private String password;
    private String oldPassword;
}