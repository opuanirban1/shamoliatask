package com.dev.aes.payloads.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DictionaryCheckerResponse {
    private List<String> mismatch;
}