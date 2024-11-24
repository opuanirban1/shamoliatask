package com.dev.aes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "files")
@NoArgsConstructor
@AllArgsConstructor
public class DocFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String fileName;
    private String fileType;
    private String keyFileName;
    private String location;
    private String status;
    private String docType;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "folder_id")
    @JsonIgnore
    private Folder folder;
//    @Column(name = "folder_id", insertable = false, updatable = false)
//    private Long folderId;
    private Long doOcrDurationInSec;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_by", insertable = false, updatable = false)
    private Long createdById;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;



}
