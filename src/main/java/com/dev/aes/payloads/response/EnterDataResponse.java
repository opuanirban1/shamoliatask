package com.dev.aes.payloads.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EnterDataResponse {

    private String message;
    private String status;

}
