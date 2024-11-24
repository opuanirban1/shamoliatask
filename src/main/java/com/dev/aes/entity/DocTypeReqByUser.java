package com.dev.aes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="doctypereqbyuser")
@Builder
public class DocTypeReqByUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="name",  length = 255 )
    private String name;

    @Column(name="confirmname",  length = 255 )
    private String confirmname;

    @Column(name="mlname",  length = 255 )
    private String mlname;


    @Column(name="status" )
    private String status;


    private String fileName;
    private String fileType;
    private String location;

    private String fileurl;

    @Column(name="create_at")
    @UpdateTimestamp
    private LocalDateTime createAt;

    @Column(name="update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    @Column(name="active",  length = 255 )
    private String active;

    @Column(name="ocrstatus",  length = 255 )
    private String ocrstatus;

    @Column(name="multipagestatus",  length = 255 )
    private String multipagestatus;

    @Column(name ="pagecount")
    private Integer pagecount;

    @Column(name="pageno")
    private String pageno;

    @Column(name="subclass")
    private String subclass;

    @Column(name="parentdoctypeid")
    private Integer parentdoctypeid;

    @Column(name="requserid")
     private Long requserid;

    @Column(name="doctypeid")
    private Long doctypeid;

}
