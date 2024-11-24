package com.dev.aes.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserSystemSettingResponse {

    private Integer  id;
    private String name;
    private Boolean userinput;
}
