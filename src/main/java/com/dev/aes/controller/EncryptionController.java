package com.dev.aes.controller;


import com.dev.aes.entity.AllEncryption;
import com.dev.aes.entity.DocTypeField;
import com.dev.aes.entity.User;
import com.dev.aes.entity.UserWiseEncryption;
import com.dev.aes.payloads.request.EncryptionRequest;
import com.dev.aes.payloads.response.EncryptionResponse;
import com.dev.aes.repository.AllencryptionRepository;
import com.dev.aes.repository.UserwiseencryptionRepository;
import com.dev.aes.service.EncryptionService;
import com.dev.aes.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/enCryption")
public class EncryptionController {

    @Autowired
    AllencryptionRepository allencryptionRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserwiseencryptionRepository userwiseencryptionRepository;

    @Autowired
    EncryptionService encryptionService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<AllEncryption>> getallConfig() {
        return new ResponseEntity<>(allencryptionRepository.getAllEncrytionData(), HttpStatus.OK);
    }

    @GetMapping("/userwiseEncryption")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<UserWiseEncryption> getUserwiseEncryptionConfig() {


        return new ResponseEntity<>(encryptionService.getUserwiseEncrytionList(), HttpStatus.OK);
    }

    //EncrytionRequest

    @PostMapping("/submitEncryption")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<EncryptionResponse> doUserwiseEncryptionAction(@Valid @RequestBody EncryptionRequest encryptionRequest ) {

        /*
        User user = userService.getCurrentuser();

        UserWiseEncryption userWiseEncryption = userwiseencryptionRepository.save(UserWiseEncryption.builder()
                .encryptionname(encryptionRequest.getEncryptioninfo())
                .userid(user.getId())
                        .password( Base64.getEncoder()
                                .encodeToString(encryptionRequest.getPassword().getBytes()) )
                .build());*/

        return new ResponseEntity<>(encryptionService.doUserwiseEncryptionAction(encryptionRequest), HttpStatus.OK);
    }
}
