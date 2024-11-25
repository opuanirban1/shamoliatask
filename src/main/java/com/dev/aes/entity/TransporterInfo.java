package com.dev.aes.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Table(name = "transporterinfo")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransporterInfo {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "createdat")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updateadat")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phoneno")
    private String phoneno;

    @Column(name = "address")
    private String address;

}
