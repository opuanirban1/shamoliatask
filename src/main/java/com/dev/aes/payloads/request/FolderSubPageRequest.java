package com.dev.aes.payloads.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class FolderSubPageRequest {

    private List<String> doctypes;
    private String status;
    private Integer pageno;
    private Integer limitperpage;

}
