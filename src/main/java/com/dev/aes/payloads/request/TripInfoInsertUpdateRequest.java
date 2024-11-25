package com.dev.aes.payloads.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
