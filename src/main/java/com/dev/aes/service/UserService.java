package com.dev.aes.service;

import com.dev.aes.entity.User;
import com.dev.aes.payloads.request.ChangePasswordDto;
import com.dev.aes.payloads.request.RegisterOrgAdminDto;
import com.dev.aes.payloads.request.RegisterUserDto;
//import com.dev.aes.payloads.response.FolderShareUserResponseDto;
import com.dev.aes.payloads.response.UserResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    List<UserResponseDto> allUsers();

    UserResponseDto getAuthencateUser();

    User getCurrentuser();

    UserResponseDto signupOrgAdmin(RegisterOrgAdminDto registerUserDto);

    UserResponseDto signupUser(RegisterUserDto dto);

    User getCurrentRootSystemAdmin();

    List<UserResponseDto> getUserRoles();

    User getCurrentRootSystemAdmin(User user);

    User findUserById(Long createdById);

    void changePassword(String email, HttpServletRequest request);

    void resetPassword(String token, ChangePasswordDto changePasswordDto);

    String verifyForgotPasswordToken(String token);

    void changePasswords(ChangePasswordDto changePasswordDto);

    //FolderShareUserResponseDto getFolderShareResponseDto(Long userId);

    List<User> getUsersForSearch();

    public User getCurrentuserByAuth();
}
