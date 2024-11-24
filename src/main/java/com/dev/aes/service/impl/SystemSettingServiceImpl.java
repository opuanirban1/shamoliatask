package com.dev.aes.service.impl;

import com.dev.aes.controller.SystemSettingController;
import com.dev.aes.entity.SysSetting;
import com.dev.aes.entity.User;
import com.dev.aes.payloads.request.UserSystemSettingInputRequest;
import com.dev.aes.payloads.response.UserSystemSettingInputResponse;
import com.dev.aes.payloads.response.UserSystemSettingResponse;
import com.dev.aes.repository.SysSettingRepository;
import com.dev.aes.repository.UserWiseSysSettingRepository;
import com.dev.aes.service.SystemSettingService;
import com.dev.aes.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SystemSettingServiceImpl implements SystemSettingService {

   @Autowired
   UserWiseSysSettingRepository userWiseSysSettingRepository;

   @Autowired
   SysSettingRepository sysSettingRepository;

   @Autowired
   UserService userService;

   private final Logger LOG = LoggerFactory.getLogger(SystemSettingServiceImpl.class);

   @Override
   public List<UserSystemSettingResponse> getUserSystemSettingResponse(){
       List<UserSystemSettingResponse>  userSystemSettingResponseList = new ArrayList<UserSystemSettingResponse>();
       Long userid=0L;
       List<SysSetting> sysSettingList = new ArrayList<SysSetting>();
       User user = userService.getCurrentuser();
       userid =  userWiseSysSettingRepository.getLatestUserSystemSettinInput(user.getId());
       sysSettingList = sysSettingRepository.getAllSystemSetting();
       if (sysSettingList.size()>0){
           for (Integer counter=0; counter< sysSettingList.size(); counter++){
               if (userid == sysSettingList.get(counter).getId()) {

                   UserSystemSettingResponse userSystemSettingResponse = new UserSystemSettingResponse(Integer.parseInt(sysSettingList.get(counter).getId().toString()),
                           sysSettingList.get(counter).getName(), true);
                   userSystemSettingResponseList.add(userSystemSettingResponse);

               }else{

                   UserSystemSettingResponse userSystemSettingResponse = new UserSystemSettingResponse(Integer.parseInt(sysSettingList.get(counter).getId().toString()),
                           sysSettingList.get(counter).getName(), false);
                   userSystemSettingResponseList.add(userSystemSettingResponse);
               }
           }//end for loop
       }// end if
       LOG.info("API responser {}",userSystemSettingResponseList );
       return userSystemSettingResponseList;
   }// end function

    @Override
    public UserSystemSettingInputResponse getUserSystemSettingInput(UserSystemSettingInputRequest userSystemSettingInputRequest)
    {
        UserSystemSettingInputResponse    userSystemSettingInputResponse  = new UserSystemSettingInputResponse();

        User user = userService.getCurrentuser();
        userWiseSysSettingRepository.insertUserWiseSysSetting(user.getId(), userSystemSettingInputRequest.getUserinput(), "inserted" );

        userSystemSettingInputResponse.setId(userSystemSettingInputRequest.getUserinput());
        userSystemSettingInputResponse.setMessage("Data provided successfully");
        userSystemSettingInputResponse.setStatus("success");


        LOG.info("API responser {}",userSystemSettingInputResponse);
        return userSystemSettingInputResponse;

    }
}
