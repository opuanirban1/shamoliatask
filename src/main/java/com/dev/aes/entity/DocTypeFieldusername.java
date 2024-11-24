package com.dev.aes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="doctypefieldusername")
public class DocTypeFieldusername {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name="name",  length = 255 )
    private String name;

    @Column(name="type", length = 255)
    private String type;

    @Column(name="map_key", length = 255)
    private String mapKey;

    @Column(name="sequence")
    private Integer sequence;

    @Column(name="language")
    private String language;

    @Column (name = "username")
    private  String username;

    @Column(name="create_at")
    @UpdateTimestamp
    private LocalDateTime create_at;


    @Column(name="update_at")
    @UpdateTimestamp
    private LocalDateTime update_at;


    @Column(name = "doctype_id")
    private Integer  doctype_id;

    @Column(name = "doctypefield_id")
    private Integer  doctypefield_id;



    private Long pageNumber;
    @Lob
    private String fileUrl;

}
