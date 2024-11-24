package com.dev.aes.service;

import com.dev.aes.payloads.request.LoginUserDto;
import com.dev.aes.payloads.request.RegisterSystemAdminDto;
import com.dev.aes.payloads.response.LoginResponse;
import com.dev.aes.payloads.response.UserResponseDto;

public interface AuthenticationService {
    UserResponseDto signupSystemAdmin(RegisterSystemAdminDto input);

    LoginResponse authenticate(LoginUserDto input);
}
