package com.dev.aes.payloads.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserDto {
    @NotBlank
    @Size(max = 1000)
    private String email;
    @NotBlank
    private String username;
    private String address;
    @NotBlank
    private String phoneNo;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
}
