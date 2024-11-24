package com.dev.aes.controller;

import com.dev.aes.payloads.request.LoginUserDto;
import com.dev.aes.payloads.request.RegisterSystemAdminDto;
import com.dev.aes.payloads.response.LoginResponse;
import com.dev.aes.payloads.response.UserResponseDto;
import com.dev.aes.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup/system-admin")
    public ResponseEntity<UserResponseDto> registerSystemAdmin(@Valid @RequestBody RegisterSystemAdminDto registerUserDto) {
        return ResponseEntity.ok(authenticationService.signupSystemAdmin(registerUserDto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserDto loginUserDto) {
        return ResponseEntity.ok(authenticationService.authenticate(loginUserDto));
    }
}