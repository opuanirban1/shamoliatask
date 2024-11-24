package com.dev.aes.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class RevokeUsrPermissionReqDto {

    @JsonProperty("folderid")
    private Integer folderid;

    @JsonProperty("userid")
    private List<Integer> userid;
}