package com.dev.aes.payloads.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class ImportDocTypeUserwiseRequest {

    private List<String> doctypes;
    private List<Long> doctypeids;
}
