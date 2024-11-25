package com.dev.aes.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Setter
@Getter
@Table(name = "tripinfo")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripInfo {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="customername")
    private String customername;

    @Column(name = "createdat")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updateadat")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "pickuplocation")
    private String pickuplocation;

    @Column(precision=11, name="pickuplat" )
    private double pickuplat;

    @Column(precision=11,  name="pickuplong" )
    private double pickuplong;

    @Column(precision=11,  name="droplat" )
    private double droplat;

    @Column(precision=11,  name="droplong" )
    private double droplong;

    @Column(name = "droplocation")
    private String droplocation;

    @Column(name = "status")
    private String status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "transporter_id")
    private TransporterInfo transporterInfo;

    @Column(name = "transporteraddtime")
    @UpdateTimestamp
    private LocalDateTime transporteraddtime;
}
