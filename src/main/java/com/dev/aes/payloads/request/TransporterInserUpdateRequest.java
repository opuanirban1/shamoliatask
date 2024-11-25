package com.dev.aes.payloads.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TransporterInserUpdateRequest {


    private Long id;
    private String name;
    private String email;
    private String phoneno;
    private String address;

}
