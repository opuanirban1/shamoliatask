package com.dev.aes.payloads.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpellCheckResponse {
    private List<IncorrectWord> incorrectWords;
}
