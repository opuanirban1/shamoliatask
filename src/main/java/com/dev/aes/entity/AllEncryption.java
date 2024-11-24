package com.dev.aes.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="allencryption")
@Builder
public class AllEncryption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name="name",  length = 255 )
    private String name;

    @Column(name="status",  length = 255 )
    private String status;


    @Column(name = "createdby")
    private Long createdById;

    @Column(name = "updateby")
    private Long updateby;


    @CreationTimestamp
    private LocalDateTime createdAt;


    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
