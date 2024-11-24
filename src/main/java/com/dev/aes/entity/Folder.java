package com.dev.aes.entity;

import com.dev.aes.constant.FolderActiveStatus;
import com.dev.aes.constant.FolderType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "folders")
@NoArgsConstructor
@AllArgsConstructor
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private FolderType type;
    private FolderActiveStatus active;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="parent_id")
    private Folder parent;

    @OneToMany(mappedBy="parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Folder> subFolders;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "folder")
    @JsonIgnore
    private Set<DocFile> docFiles;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_byy")
    private User createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
