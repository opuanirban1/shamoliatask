package com.dev.aes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="doctype")
@Builder
public class DocType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="name",  length = 255 )
    private String name;

    private String fileName;

    private String fileType;

    private String keyFileName;

    private String location;

    @OneToMany(mappedBy = "docType")
    private Set<DocTypeField> docTypeFields = new HashSet<>();

    @Column(name="create_at")
    @UpdateTimestamp
    private LocalDateTime createAt;

    private Boolean isMultiPageDoc = false;

    @Column(name="update_at")
    @UpdateTimestamp
    private LocalDateTime updateAt;

    private String ocrstatus;

    private Long docTypeId;

    private Long createdBy;
}
