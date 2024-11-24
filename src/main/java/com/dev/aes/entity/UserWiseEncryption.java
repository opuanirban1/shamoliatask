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
@Table(name="userwiseencryption")
@Builder
public class UserWiseEncryption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="encryptionname",  length = 255 )
    private String encryptionname;

    @Column(name="status",  length = 255 )
    private String status;

    @Column(name="password",  length = 255 )
    private String password;

    @Column(name = "userid")
    private Long userid;

    @Column(name = "updateby")
    private Long updateby;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
