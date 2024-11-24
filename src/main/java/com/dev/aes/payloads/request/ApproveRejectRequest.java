package com.dev.aes.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ApproveRejectRequest {

    @JsonProperty("userid")
    private Long userid;

    @JsonProperty("doctypeid")
    private Long doctypeid;

    @JsonProperty("status")
    private String status;

    @JsonProperty("sourcereqid")
    private Long sourcereqid;


    @JsonProperty("name")
    private String name;


}
