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
@Table(name="doctypeuserwise")
@Builder
public class DocTypeUserWise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name="name",  length = 255 )
    private String name;

    @Column(name="active",  length = 255 )
    private String active;

    @Column(name="status",  length = 255 )
    private String status;

    @Column(name="type", length = 255)
    private String type;


    @Column(name="fileName", length = 255)
    private String fileName;

    @Column(name="fileType", length = 255)
    private String fileType;

    @Column(name="keyFileName", length = 255)
    private String keyFileName;

    @Column(name="location", length = 255)
    private String location;

    @Column(name="create_at")
    @UpdateTimestamp
    private LocalDateTime createAt;


    @Column(name="isMultiPageDoc")
    private Boolean isMultiPageDoc = false;

    @Column(name="update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;


    @Column(name="createdby")
    private Long createdby;

    @Column(name="doctypeid")
    private Long doctypeid;


}
