package com.dev.aes.payloads.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddToDictionaryParserAnnotatorDto {
    private Long id;
    private Long userId;
    private String fileUrl;
    private String correctWord;
    private String wrongWord;
    private String fileName;
    private String fileType;
    private String location;
}
