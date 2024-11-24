package com.dev.aes.payloads.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncorrectWord {
    private String token;
    private Integer startIndex;
}