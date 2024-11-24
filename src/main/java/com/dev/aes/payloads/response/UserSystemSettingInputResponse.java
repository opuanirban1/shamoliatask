package com.dev.aes.payloads.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserSystemSettingInputResponse {

    private Integer id;
    private String message;
    private String status;
}
