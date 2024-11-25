package com.dev.aes.payloads.request;

import com.dev.aes.entity.TransporterInfo;
import com.dev.aes.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class TripInfoInsertUpdateRequest {


    private Long id;

    private String pickuplocation;

    private double pickuplat;


    private double pickuplong;


    private double droplat;


    private double droplong;


    private String droplocation;


    private String status;

    private String customername;


}
