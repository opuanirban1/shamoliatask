package com.dev.aes.payloads.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalSearchDto {
    private Long id;
    private String name;
    private String type;
    private String folderid;
}
