package com.dev.aes.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class DocTypeConfigData {

    private String sequence;
    private String name;
    private String type;
    private String mapKey;
    private String lanType;

}
