package com.dev.aes.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Table(name = "sys_setting")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class SysSetting {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String status;

}
