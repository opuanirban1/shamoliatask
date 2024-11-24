package com.dev.aes.payloads.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class NextFileIdByDoctypeSubFolderRes {

    @JsonProperty("fileids")
    private List<Integer> fileids;

}
