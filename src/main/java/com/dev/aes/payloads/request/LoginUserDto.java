package com.dev.aes.payloads.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDto {
    @NotBlank
    @Size(max = 1000)
    private String email;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}