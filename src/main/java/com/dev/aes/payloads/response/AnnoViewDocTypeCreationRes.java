package com.dev.aes.payloads.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Data
@Getter
@Setter
public class AnnoViewDocTypeCreationRes {

    private Long id;
    private String name;
    private String confirmname;
    private String mlname;
    private String status;
    private String fileName;
    private String fileType;
    private String location;
    private String fileurl;
    private String createAt;
    private String updateAt;
    private String active;
    private String ocrstatus;
    private String multipagestatus;
    private Integer pagecount;
    private String pageno;
    private String subclass;
    private Integer parentdoctypeid;
    private Long requserid;
    private Long reqid;
    private Long sourcereqid;
    private String creatorname;
}
