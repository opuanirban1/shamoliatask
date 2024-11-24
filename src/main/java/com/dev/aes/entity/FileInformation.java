package com.dev.aes.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
//@AllArgsConstructor
@Table(name="fileinformation")
public class FileInformation {


    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String filename ;
    private String active;
    private String status ;
    private String type ;
    private String location;
    private Integer doctype_id ;
    private String doctype_text ;
    private String doctype_detect_status ;
    private String ocr_detect_status ;
    private Integer file_id ;
    private Date create_at;
	private Date update_at;
	private Integer create_by;
	private Integer update_by;

}