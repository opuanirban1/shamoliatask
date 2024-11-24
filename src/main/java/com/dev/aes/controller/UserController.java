package com.dev.aes.controller;

import com.dev.aes.payloads.request.ChangePasswordDto;
import com.dev.aes.payloads.request.ForgotPasswordDTO;
import com.dev.aes.payloads.request.RegisterOrgAdminDto;
import com.dev.aes.payloads.request.RegisterUserDto;
import com.dev.aes.payloads.response.UserResponseDto;
import com.dev.aes.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    @Value("${application.frontend.url}")
    private String applicationFrontendUrl;

    public static final String CHANGE_PASSWORD_URL = "/auth/forgot-password/";
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> authenticatedUser() {
        return ResponseEntity.ok(userService.getAuthencateUser());
    }

    @PostMapping("/org-admin")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<UserResponseDto> registerOrgAdmin(@Valid @RequestBody RegisterOrgAdminDto dto) {
        return ResponseEntity.ok(userService.signupOrgAdmin(dto));
    }

    @PostMapping("/user")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody RegisterUserDto dto) {
        return ResponseEntity.ok(userService.signupUser(dto));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> allUsers() {
        return ResponseEntity.ok(userService.allUsers());
    }

    @GetMapping("/user_role")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getUserRoles() {
        return ResponseEntity.ok(userService.getUserRoles());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO, HttpServletRequest request) {
        userService.changePassword(forgotPasswordDTO.getEmail(), request);
        return new ResponseEntity<>("An email has been sent to " + forgotPasswordDTO.getEmail() +
                " with a verification link.", HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        userService.changePasswords(changePasswordDto);
        return new ResponseEntity<>("Password has been changed successfully.", HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> updateCredential(@Valid @RequestBody ChangePasswordDto changePasswordDto, @RequestParam("token") String token) {
        userService.resetPassword(token, changePasswordDto);
        return new ResponseEntity<>("Password has been changed successfully.", HttpStatus.OK);
    }

    @PostMapping("/verify/reset-password-token")
    public ResponseEntity<String> verifyResetPasswordToken(@RequestParam("token") String token){
        userService.verifyForgotPasswordToken(token);
        return new ResponseEntity<>("Valid Token", HttpStatus.OK);
    }

    @GetMapping("/change-password-view")
    public RedirectView changePasswordView(@RequestParam("token") String token) {
        String email = userService.verifyForgotPasswordToken(token);
        return new RedirectView(applicationFrontendUrl + CHANGE_PASSWORD_URL + token);
    }
}