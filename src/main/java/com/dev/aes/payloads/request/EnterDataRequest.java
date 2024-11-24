package com.dev.aes.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
@Data
public class EnterDataRequest {


    @JsonProperty("action")
    private String action;

    @JsonProperty("doctypeName")
    private String  doctypeName;

    @JsonProperty("doctypeId")
    private Long doctypeId;

    @JsonProperty("fileId")
    private Long fileId;

    @JsonProperty("doctypemainclassId")
    private Integer doctypemainclassId;

    @JsonProperty("mapkey")
    private HashMap<Integer, String> mapkey;

    @JsonProperty("name")
    private HashMap<Integer, String> name;

    @JsonProperty("type")
    private HashMap<Integer, String> type;

    @JsonProperty("doctypeFieldId")
    private HashMap<Integer, Long> doctypeFieldId;

    @JsonProperty("inputdata")
    private HashMap<Integer, String> inputdata;

    @JsonProperty("language")
    private HashMap<Integer, String> language;

    /*@JsonProperty("createdby")
    private Integer createdby;*/

    @JsonProperty("filecontentfieldid")
    private HashMap<Integer, Long> filecontentfieldid;


}
