package com.dev.aes.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ParseConfigWithMapkey implements ParseConfigWithMapKeyDto {


    private String sequence;
    private String name;
    private String type;
    private String mapKey;
    private String lanType;


}
