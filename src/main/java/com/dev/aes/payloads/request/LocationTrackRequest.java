package com.dev.aes.payloads.request;

import com.dev.aes.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class LocationTrackRequest {

    private Long id;
    private double locationlat;
    private double locationlong;


}
