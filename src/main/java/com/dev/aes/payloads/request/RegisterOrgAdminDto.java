package com.dev.aes.payloads.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterOrgAdminDto {
    @NotBlank
    @Size(max = 1000)
    private String email;
    @NotBlank
    @Size(max = 100)
    private String username;
    @Size(max = 1000)
    private String address;
    @NotBlank
    @Size(max = 1000)
    private String phoneNo;
    @NotBlank
    @Size(min = 8, max = 100)
    private String password;
    private String businessName;
    private String businessEmail;
    private String businessMobileNumber;
}
