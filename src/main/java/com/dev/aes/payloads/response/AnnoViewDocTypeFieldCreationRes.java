package com.dev.aes.payloads.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class AnnoViewDocTypeFieldCreationRes {

    private Long id;
    private String name;
    private String type;
    private String mapKey;
    private Integer sequence;
    private String create_at;
    private String update_at;
    private String language;
    private Long docTypeId;
    private Long doctypepage_id;
    private Long requserid;
    private String status;
    private Long reqid;
    private Long sourcereqid;


}
