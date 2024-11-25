package com.dev.aes.payloads.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LocationTrackRequest {

    private Long id;
    private double locationlat;
    private double locationlong;


}
