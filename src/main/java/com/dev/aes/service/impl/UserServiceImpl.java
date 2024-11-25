package com.dev.aes.service.impl;

import com.dev.aes.email_service.EmailSenderUtil;
import com.dev.aes.entity.PasswordResetToken;
import com.dev.aes.entity.Role;
import com.dev.aes.entity.User;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.ChangePasswordDto;
import com.dev.aes.payloads.request.RegisterOrgAdminDto;
import com.dev.aes.payloads.request.RegisterUserDto;
//import com.dev.aes.payloads.response.FolderShareUserResponseDto;
import com.dev.aes.payloads.response.UserResponseDto;
import com.dev.aes.repository.PasswordTokenRepository;
import com.dev.aes.repository.UserRepository;
import com.dev.aes.security.UserDetailsImpl;
import com.dev.aes.service.RoleService;
import com.dev.aes.service.UserService;
import com.dev.aes.util.LocalServerProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    @Value("${application.server.url}")
    private String applicationServerUrl;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final PasswordTokenRepository passwordTokenRepository;
    private final LocalServerProperties localServerProperties;
    private final EmailSenderUtil emailSenderUtil;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder, RoleService roleService,
                           PasswordTokenRepository passwordTokenRepository,
                           LocalServerProperties localServerProperties, EmailSenderUtil emailSenderUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.passwordTokenRepository = passwordTokenRepository;
        this.localServerProperties = localServerProperties;
        this.emailSenderUtil = emailSenderUtil;
    }

    @Override
    public List<UserResponseDto> allUsers() {
        User currentuser = getCurrentuser();
        List<User> users = userRepository.findAllByParent(currentuser);
        return users.stream().map(this::convertToResponseDto).toList();
    }

    @Override
    public UserResponseDto getAuthencateUser() {
        return convertToResponseDto(getCurrentuser());
    }

    @Override
    public User getCurrentuser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(principal.getId())
                .orElseThrow(() -> new OcrDmsException("User Not Found", HttpStatus.BAD_REQUEST));
    }

    @Override
    public User getCurrentuserByAuth() {
        User user = new User();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String useremail =  authentication.getName();

        user =  userRepository.findUserByEmail(useremail);

        if (user == null){

            new OcrDmsException("User Not Found", HttpStatus.BAD_REQUEST);

        }

        return user;
        /*return userRepository.findUserByEmail(useremail)
                .orElseThrow(() -> new OcrDmsException("User Not Found", HttpStatus.BAD_REQUEST));*/
    }


    @Override
    public User getCurrentRootSystemAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        User current = userRepository.findById(principal.getId()).orElse(null);
        while (current != null) {
            List<String> roles = current.getRoles().stream().map(Role::getName).toList();
            if (roles.contains("ROLE_SYSTEM_ADMIN")) {
                return current;
            }
            current = current.getParent();
        }
        return null;
    }

    @Override
    public List<UserResponseDto> getUserRoles() {
        User currentuser = getCurrentuser();
        System.out.println(" User id"+currentuser.getId());
        List<User> users = userRepository.findAllByParent(currentuser);
        List<UserResponseDto> userResponseDtos = users.stream().map(this::convertToResponseDto).toList();
        List<UserResponseDto> response = new ArrayList<>(userResponseDtos.stream().filter(user -> user.getRoles().contains("ROLE_USER")).toList());
        if (convertToResponseDto(currentuser).getRoles().contains("ROLE_SYSTEM_ADMIN")){
            users = users.stream().filter(user -> convertToResponseDto(user).getRoles().contains("ROLE_ORG_ADMIN")).toList();
            if(!users.isEmpty()){
                for (User user: users){
                   // List<User> orgUsers = userRepository.findAllByParent(user);
                   //  if (!orgUsers.isEmpty()){
                     //   for (User orgUser: orgUsers){
                           // response.add(convertToResponseDto(orgUser));
                       // }
                   // }
                    response.add(convertToResponseDto(user));
                }
            }
        }

        return response;
    }

    @Override
    public User getCurrentRootSystemAdmin(User user) {
        while (user != null) {
            List<String> roles = user.getRoles().stream().map(Role::getName).toList();
            if (roles.contains("ROLE_SYSTEM_ADMIN")) {
                return user;
            }
            user = user.getParent();
        }
        return null;
    }

    @Override
    public User findUserById(Long createdById) {
        return userRepository.findById(createdById).orElse(null);
    }

    @Override
    public void changePassword(String email, HttpServletRequest request) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new OcrDmsException("User not found");
        }
        String url = checkIfAnyPreviousTokenGeneratedForThisUser(email, request);
        emailSenderUtil.sendPasswordVerificationEmail(url, email);
    }

    @Override
    public void resetPassword(String token, ChangePasswordDto changePasswordDto) {
        String email = verifyForgotPasswordToken(token);
        Optional<PasswordResetToken> previousToken = passwordTokenRepository.findByEmail(email);
        previousToken.ifPresent(passwordTokenRepository::delete);
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) throw new OcrDmsException("No user credential found with this token");
        User previousCredential = userOptional.get();
        previousCredential.setPassword(changePasswordDto.getPassword());
        update(previousCredential);
    }

    public void update(User user) {
        Optional<User> existingUserCredentialOp = userRepository.findByEmail(user.getEmail());
        if (existingUserCredentialOp.isPresent()) {
            User existingUserCredential =  existingUserCredentialOp.get();
            existingUserCredential.setPassword(passwordEncoder.encode(user.getPassword()));
            User updatedUserCredential = userRepository.save(existingUserCredential);
            emailSenderUtil.passwordChangeConfirmationEmail(updatedUserCredential.getEmail());
        }
    }

    @Override
    public String verifyForgotPasswordToken(String token){
        Optional<PasswordResetToken> passwordResetToken = passwordTokenRepository.findByToken(token);
        if(passwordResetToken.isEmpty()) throw new OcrDmsException("Invalid token found");
        if(isTokenExpired(passwordResetToken.get())) throw new OcrDmsException("Token validity expired");
        return passwordResetToken.get().getEmail();
    }

    @Override
    public void changePasswords(ChangePasswordDto changePasswordDto) {
        User currentuser = getCurrentuser();

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), currentuser.getPassword())) {
            throw new OcrDmsException("Old password mismatch found!");
        }
        currentuser.setPassword(changePasswordDto.getPassword());
        update(currentuser);
    }

  /*  @Override
    public FolderShareUserResponseDto getFolderShareResponseDto(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            return FolderShareUserResponseDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .build();
        }
        return null;
    }*/

    @Override
    public List<User> getUsersForSearch() {
        List<User> users = new ArrayList<>();
        User currentUser = getCurrentuser();
        List<String> roles = getRoles(currentUser);
        if (roles.contains("ROLE_SYSTEM_ADMIN")){
            users = userRepository.findAllByParent(currentUser);

            users.add(currentUser);
            return getAllRelatedUsers(users, currentUser);
        }
        if (roles.contains("ROLE_ORG_ADMIN")){
          User orgParentUser = currentUser.getParent();
          users = userRepository.findAllByParent(orgParentUser);
            return getAllRelatedUsers(users, currentUser);
        }
        if (roles.contains("ROLE_ORG_USER")){
            User userParentuser = getCurrentRootSystemAdmin(currentUser);
            users = userRepository.findAllByParent(userParentuser);
            return getAllRelatedUsers(users, currentUser);
        }
        return List.of();
    }

    private List<User> getAllRelatedUsers(List<User> users, User currentUser) {
        List<User> finalOrgUsers = new ArrayList<>();
        for (User user: users){
            List<String> orgRoles = getRoles(user);
            if (orgRoles.contains("ROLE_ORG_ADMIN")){
                List<User> orgUsers = userRepository.findAllByParent(user);
                if (!orgUsers.isEmpty()) {
                    for (User orgUser: orgUsers){
                        finalOrgUsers.add(orgUser);
                    }
                }
            }
        }
        users.add(currentUser);
        users.addAll(finalOrgUsers);
        return users;
    }

    private List<String> getRoles(User user) {
        return user.getRoles().stream().map(Role::getName).toList();
    }

    public Boolean isTokenExpired(PasswordResetToken token){
        Date currentDate = new Date();
        return !token.getExpiryDate().after(currentDate);
    }

    public String checkIfAnyPreviousTokenGeneratedForThisUser(String emailAddress, HttpServletRequest request){
        Optional<PasswordResetToken> previousToken = passwordTokenRepository.findByEmail(emailAddress);
        previousToken.ifPresent(passwordTokenRepository::delete);
        return generateNewToken(emailAddress, request);
    }

    public String generateNewToken(String email, HttpServletRequest request){
        String randomToken = UUID.randomUUID().toString();
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE,3);
        Date currentDatePlusThree = c.getTime();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new OcrDmsException("No user found with this email");
        }
        String savedToken = passwordTokenRepository.save(new PasswordResetToken(email, currentDatePlusThree, randomToken, user.get())).getToken();
//        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/api/v1/users/change-password-view?token=" + savedToken;
        return applicationServerUrl + "/api/v1/users/change-password-view?token=" + savedToken;
    }

    @Override
    public UserResponseDto signupOrgAdmin(RegisterOrgAdminDto dto) {
        checkExistingUser(RegisterUserDto.builder().username(dto.getUsername()).email(dto.getEmail()).phoneNo(dto.getPhoneNo()).build());
        var user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setBusinessEmail(dto.getBusinessEmail());
        user.setBusinessMobileNumber(dto.getBusinessMobileNumber());
        user.setPhoneNo(dto.getPhoneNo());
        user.setBusinessName(dto.getBusinessName());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(roleService.findByName("ROLE_ORG_ADMIN")));
        user.setParent(getCurrentuser());
        User saveUser = userRepository.save(user);
        return convertToResponseDto(saveUser);
    }

    @Override
    public UserResponseDto signupUser(RegisterUserDto dto) {
        checkExistingUser(dto);
        var user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAddress(dto.getAddress());
        user.setPhoneNo(dto.getPhoneNo());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(Set.of(roleService.findByName("ROLE_USER")));
        user.setParent(getCurrentuser());
        User saveUser = userRepository.save(user);
        return convertToResponseDto(saveUser);
    }

    private void checkExistingUser(RegisterUserDto dto) {
        Optional<User> existingUser = userRepository.findByUsername(dto.getUsername());
        if (existingUser.isPresent()) {
            throw new OcrDmsException("Username already exist.");
        }
        existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            throw new OcrDmsException("Email already exist.");
        }
        existingUser = userRepository.findByPhoneNo(dto.getPhoneNo());
        if (existingUser.isPresent()) {
            throw new OcrDmsException("Phone no. already exist.");
        }
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

