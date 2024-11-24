package com.dev.aes.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MLTransAPIResponse {

    @JsonProperty("message")
    private String message;
}
