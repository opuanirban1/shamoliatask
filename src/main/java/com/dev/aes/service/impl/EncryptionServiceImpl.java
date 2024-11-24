package com.dev.aes.service.impl;

import com.dev.aes.entity.User;
import com.dev.aes.entity.UserWiseEncryption;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.EncryptionRequest;
import com.dev.aes.payloads.response.EncryptionResponse;
import com.dev.aes.repository.AllencryptionRepository;
import com.dev.aes.repository.UserwiseencryptionRepository;
import com.dev.aes.service.EncryptionService;
import com.dev.aes.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

@Service
public class EncryptionServiceImpl implements EncryptionService {

    @Autowired
    AllencryptionRepository allencryptionRepository;

    @Autowired
    UserService userService;

    @Autowired
    UserwiseencryptionRepository userwiseencryptionRepository;

    @Override
    @Transactional
    @Modifying
    public EncryptionResponse doUserwiseEncryptionAction (EncryptionRequest encryptionRequest){

        User user = userService.getCurrentuser();
        EncryptionResponse encryptionResponse = new EncryptionResponse();
        UserWiseEncryption userWiseEncryption = new UserWiseEncryption();

         Integer checkinsert =0, checkinsert2=0;

       // @Param("encryptionname") String encryptionname,@Param("id") Long id, @Param("userid") String userid);

        checkinsert = userwiseencryptionRepository.countUserwiseEncryptionList(user.getId(), Base64.getEncoder()
                .encodeToString(encryptionRequest.getPassword().getBytes()));


        if (encryptionRequest.getAction().equalsIgnoreCase("disable") && checkinsert>0){

             //userwiseencryptionRepository.updateUserWiseEncryption("", encryptionRequest.getId(), user.getId() );

            userwiseencryptionRepository.updateDisableDeleteUserWiseEncryption(encryptionRequest.getId());

             encryptionResponse.setMessage("Succesfully disabled.");
             encryptionResponse.setStatus("success");

             return encryptionResponse;
        }



        System.out.println("Anirban checkinsert"+checkinsert+" id"+checkinsert);

        if (checkinsert<=0 && encryptionRequest.getId()== 0){

            //update



           /* checkinsert2 = userwiseencryptionRepository.countUserwiseEncryptionListWithUserid(user.getId());

            if (checkinsert2<=0) {*/

                userWiseEncryption = userwiseencryptionRepository.save(UserWiseEncryption.builder()
                        .id(encryptionRequest.getId())
                        .encryptionname(encryptionRequest.getEncryptioninfo())
                        .userid(user.getId())
                        .password(Base64.getEncoder()
                                .encodeToString(encryptionRequest.getPassword().getBytes()))
                        .build());

                encryptionResponse.setMessage("Encryption inserted");
                encryptionResponse.setStatus("success");

                return encryptionResponse;

           /* }else {

                userwiseencryptionRepository.updateUserWiseEncryption(encryptionRequest.getEncryptioninfo(), encryptionRequest.getId(), user.getId() );

                encryptionResponse.setMessage("Encryption data updated.");
                encryptionResponse.setStatus("success");

                return encryptionResponse;



            }*/



        }else   if (checkinsert<=0 && encryptionRequest.getId() > 0) {

            //userwiseencryptionRepository.updateUserWiseEncryption(encryptionRequest.getEncryptioninfo(), encryptionRequest.getId(), user.getId() );

            /*encryptionResponse.setMessage("Password did not matched");
            encryptionResponse.setStatus("error");

            return encryptionResponse;*/

            throw new OcrDmsException("Password did not matched");

        }
        else   if (checkinsert>0 && encryptionRequest.getId() > 0) {



            userwiseencryptionRepository.updateUserWiseEncryption(encryptionRequest.getEncryptioninfo(), encryptionRequest.getId(), user.getId() );

            encryptionResponse.setMessage("Encryption data updated.");
            encryptionResponse.setStatus("success");

            return encryptionResponse;





           /* checkinsert2 = userwiseencryptionRepository.countUserwiseEncryptionListWithUserid(user.getId());

            if (checkinsert2<=0) {

                userWiseEncryption = userwiseencryptionRepository.save(UserWiseEncryption.builder()
                        .id(encryptionRequest.getId())
                        .encryptionname(encryptionRequest.getEncryptioninfo())
                        .userid(user.getId())
                        .password(Base64.getEncoder()
                                .encodeToString(encryptionRequest.getPassword().getBytes()))
                        .build());

                encryptionResponse.setMessage("Encryption inserted");
                encryptionResponse.setStatus("success");

                return encryptionResponse;

            }else{

                userwiseencryptionRepository.updateUserWiseEncryption(encryptionRequest.getEncryptioninfo(), encryptionRequest.getId(), user.getId() );

                encryptionResponse.setMessage("Encryption data updated.");
                encryptionResponse.setStatus("success");

                return encryptionResponse;



            }*/
        }else{

            encryptionResponse.setMessage("Password did not matched");
            encryptionResponse.setStatus("error");

            return encryptionResponse;


        }


        //return encryptionResponse;

    }

    @Override
    public UserWiseEncryption getUserwiseEncrytionList(/*, String password*/){


        User user = userService.getCurrentuser();
        return userwiseencryptionRepository.getUserwiseEncrytionList(user.getId()/*, String password*/);

    }
}
