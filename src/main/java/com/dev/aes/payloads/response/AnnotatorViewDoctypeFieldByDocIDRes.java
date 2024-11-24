package com.dev.aes.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class AnnotatorViewDoctypeFieldByDocIDRes {
    private Long id;
    private String name;
    private String type;
    private String mapKey;
    private Integer sequence;
    private String create_at;
    private String update_at;
    private String language;
    private Integer  docTypeId;
    private Integer doctypepage_id;


}
