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
public class DocTypeCreateMainClassDto {

    @JsonProperty("name")
    private String name;

    @JsonProperty("ocrstatus")
    private String ocrstatus;

    @JsonProperty("pagecount")
    private Integer pagecount;

    @JsonProperty("multipagestatus")
    private String multipagestatus;

    @JsonProperty("sourcereqid")
    private Long  sourcereqid;

    @JsonProperty("creatorname")
    private String  creatorname;

    @JsonProperty("creatorid")
    private Long creatorid;

}
