package com.dev.aes.controller;

import com.dev.aes.entity.DocFile;
import com.dev.aes.payloads.request.UserSystemSettingInputRequest;
import com.dev.aes.payloads.response.UserSystemSettingInputResponse;
import com.dev.aes.payloads.response.UserSystemSettingResponse;
import com.dev.aes.service.FileService;
import com.dev.aes.service.SystemSettingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/setting")
public class SystemSettingController {

    private final Logger LOG = LoggerFactory.getLogger(SystemSettingController.class);

    @Autowired
    SystemSettingService systemSettingService;

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<UserSystemSettingResponse>> getUserSyystemSettingResponse() throws Exception {

        LOG.info("API endpoint /api/v1/setting/user");
       /* List<UserSystemSettingResponse> userSystemSettingResponseList = new ArrayList<UserSystemSettingResponse>();

        UserSystemSettingResponse  userSystemSettingResponse = new UserSystemSettingResponse(1,"No AI", false);
        userSystemSettingResponseList.add(userSystemSettingResponse);

        UserSystemSettingResponse  userSystemSettingResponse1 = new UserSystemSettingResponse(2,"Only Classification", true);
        userSystemSettingResponseList.add(userSystemSettingResponse1);

        UserSystemSettingResponse  userSystemSettingResponse2 = new UserSystemSettingResponse(3,"Automated Version", false);
        userSystemSettingResponseList.add(userSystemSettingResponse2);*/


        return new ResponseEntity<List<UserSystemSettingResponse>>( systemSettingService.getUserSystemSettingResponse(), HttpStatus.OK);
    }


    @PostMapping("/userinput")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<UserSystemSettingInputResponse> getUserSyystemSettingReq(@Valid @RequestBody  UserSystemSettingInputRequest userSystemSettingInputRequest) throws Exception {

        LOG.info("API endpoint /api/v1/setting/userinput");

       /* UserSystemSettingInputResponse    userSystemSettingInputResponse  = new UserSystemSettingInputResponse();

        userSystemSettingInputResponse.setId(userSystemSettingInputRequest.getUserinput());
        userSystemSettingInputResponse.setMessage("Data provided successfully");
        userSystemSettingInputResponse.setStatus("success");*/
        return new ResponseEntity<UserSystemSettingInputResponse>(  systemSettingService.getUserSystemSettingInput(userSystemSettingInputRequest) , HttpStatus.OK);
    }

}
