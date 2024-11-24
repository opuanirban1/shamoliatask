package com.dev.aes.service.impl;

import com.dev.aes.entity.Role;
import com.dev.aes.entity.User;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.LoginUserDto;
import com.dev.aes.payloads.request.RegisterSystemAdminDto;
import com.dev.aes.payloads.response.LoginResponse;
import com.dev.aes.payloads.response.UserResponseDto;
import com.dev.aes.repository.UserRepository;
import com.dev.aes.security.JwtService;
import com.dev.aes.security.UserDetailsImpl;
import com.dev.aes.service.AuthenticationService;
import com.dev.aes.service.FolderService;
import com.dev.aes.service.RoleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RoleService roleService;
    private final FolderService folderService;

    @Autowired
    public AuthenticationServiceImpl(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            JwtService jwtService, RoleService roleService,
            FolderService folderService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleService = roleService;
        this.folderService = folderService;
    }

    @Override
    @Transactional
    public UserResponseDto signupSystemAdmin(RegisterSystemAdminDto dto) {

        Optional<User> userOptional = userRepository.findByEmail(dto.getEmail());
        if (userOptional.isPresent()) {
            throw new OcrDmsException("Email already exists.", HttpStatus.CONFLICT);
        }
        userOptional = userRepository.findByUsername(dto.getUsername());
        if (userOptional.isPresent()) {
            throw new OcrDmsException("Username already exists.", HttpStatus.CONFLICT);
        }
        userOptional = userRepository.findByPhoneNo(dto.getPhoneNo());
        if (userOptional.isPresent()) {
            throw new OcrDmsException("Phone number already exists.", HttpStatus.CONFLICT);
        }

        var user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setPhoneNo(dto.getPhoneNo());
        user.setRoles(Set.of(roleService.findByName("ROLE_SYSTEM_ADMIN")));
        User saveUser = userRepository.save(user);
        folderService.createRootFolder(saveUser);
        return convertToResponseDto(saveUser);
    }

    @Override
    public LoginResponse  authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        User user = userRepository.findByEmail(input.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(new UserDetailsImpl(user));

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setRoles(user.getRoles().stream().map(Role::getName).toList());
        return loginResponse;
    }

    private UserResponseDto convertToResponseDto(User entity) {
        return UserResponseDto.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .phoneNo(entity.getPhoneNo())
                .businessEmail(entity.getBusinessEmail())
                .businessMobileNumber(entity.getBusinessMobileNumber())
                .businessName(entity.getBusinessName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .roles(entity.getRoles().stream().map(Role::getName).toList())
                .build();
    }
}
