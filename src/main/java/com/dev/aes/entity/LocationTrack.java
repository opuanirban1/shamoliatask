package com.dev.aes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Table(name = "locationtrack")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class LocationTrack {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "createdat")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(precision=11, name="locationlat" )
    private double locationlat;

    @Column(precision=11,  name="locationlong" )
    private double locationlong;


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
