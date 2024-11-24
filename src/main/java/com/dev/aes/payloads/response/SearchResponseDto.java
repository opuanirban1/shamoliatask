package com.dev.aes.payloads.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponseDto {
    private Long id;
    private String type;
    private String content;
}
