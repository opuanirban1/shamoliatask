package com.dev.aes.payloads.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocTypeFieldResponseDto {
    private Integer sequence;
    private String name;
    private String type;
    private String mapKey;
    private Long doctypepage_id;
    private String fileUrl;
}
