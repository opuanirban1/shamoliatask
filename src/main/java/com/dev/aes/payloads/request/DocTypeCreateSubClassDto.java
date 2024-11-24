package com.dev.aes.payloads.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocTypeCreateSubClassDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("pageno")
    private String pageno;

    @JsonProperty("subclass")
    private String subclass;

    @JsonProperty("parentdoctypeid")
    private Integer parentdoctypeid;

    @JsonProperty("sourcereqid")
    private Long  sourcereqid;

    @JsonProperty("creatorname")
    private String  creatorname;

    @JsonProperty("creatorid")
    private Long creatorid;
}
