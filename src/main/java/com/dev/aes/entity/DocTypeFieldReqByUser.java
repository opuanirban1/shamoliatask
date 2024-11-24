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
@Table(name="doctypefieldreqbyuser")
@Builder
public class DocTypeFieldReqByUser {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "type", length = 255)
    private String type;

    @Column(name = "map_key", length = 255)
    private String mapKey;

    @Column(name = "status", length = 255)
    private String status;



    @Column(name = "sequence")
    private Integer sequence;

    @Column(name = "create_at")
    @UpdateTimestamp
    private LocalDateTime create_at;


    @Column(name = "update_at")
    @UpdateTimestamp
    private LocalDateTime update_at;

    @Column(name="language", length = 255)
    private String language;


    @Column(name = "doctype_id")
    private Long docTypeId;

    @Column(name = "doctypepage_id")
    private Long doctypepage_id;


    @Column(name="requserid")
    private Long requserid;
}
