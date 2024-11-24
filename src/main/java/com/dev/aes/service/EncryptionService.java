package com.dev.aes.service;

import com.dev.aes.entity.UserWiseEncryption;
import com.dev.aes.payloads.request.EncryptionRequest;
import com.dev.aes.payloads.response.EncryptionResponse;

import java.util.List;

public interface EncryptionService {

    EncryptionResponse doUserwiseEncryptionAction (EncryptionRequest encryptionRequest);

    UserWiseEncryption getUserwiseEncrytionList(/*Long userid*/ /*, String password*/);
}
