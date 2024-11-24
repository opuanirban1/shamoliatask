package com.dev.aes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fileContentField")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileContentField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 255)
    private String name;

    @Column(name = "ocr_value", columnDefinition = "TEXT")
    private String ocrValue;

    @Column(name = "type", length = 255)
    private String type;

    @Column(name = "doctype_id")
    private Long doctypeId;

    @Column(name= "doctype_field_id")
    private Long doctypeFieldId;

    @Column(name = "sequence")
    private Integer  sequence;

    @Column(name = "file_id")
    private Long fileId;

    @Column(name="language")
    private String language;

    @Column(name="mapkey")
    private String mapkey;

    @Column(name="doctypemainclassid")
    private Integer doctypemainclassid;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "create_at")
    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(name = "update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;
}