package com.dev.aes.payloads.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class FolderSharePaginationRequest {

    private List<String> doctypes;
    private Integer pageno;
    private Integer limitperpage;
}
