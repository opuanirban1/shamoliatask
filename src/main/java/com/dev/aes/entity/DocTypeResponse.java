package com.dev.aes.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DocTypeResponse {
    private DocType docType;
    private List<Long> pageNumbers = new ArrayList<Long>();
}
