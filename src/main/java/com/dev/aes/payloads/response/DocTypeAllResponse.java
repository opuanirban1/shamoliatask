package com.dev.aes.payloads.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DocTypeAllResponse {


    private Long id;
    private String name;
    private String confirmname;
    private String mlname;
    private String status;
    private String fileName;
    private String fileType;
    private String location;
    private String fileurl;
    private String  createAt;
    private String updateAt;
    private String active;
    private String ocrstatus;
    private String multipagestatus;
    private Integer pagecount;
    private String pageno;
    private String subclass;
    private Integer parentdoctypeid;
}
