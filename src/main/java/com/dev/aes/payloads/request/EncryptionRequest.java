package com.dev.aes.payloads.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EncryptionRequest {

    private Long id;
    private String encryptioninfo;
    private String password;
    private String action;

}
