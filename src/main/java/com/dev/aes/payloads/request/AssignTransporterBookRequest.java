package com.dev.aes.payloads.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class AssignTransporterBookRequest {

    private Long tripid;
    private Long transporterid;
    private String status;
}
