package com.dev.aes.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocTypeFieldCreateDto {
    @NotBlank
    private String name;
    @NotBlank
    private String type;
    private String map_key;
    private Integer sequence;
    private Long  sourcereqid;
}
