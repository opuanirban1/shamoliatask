package com.dev.aes.payloads.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocTypeResponseDto {
    private Long id;
    private String name;
    private String multipagestatus;
    private String subclass;
    private String ocrstatus;
}
