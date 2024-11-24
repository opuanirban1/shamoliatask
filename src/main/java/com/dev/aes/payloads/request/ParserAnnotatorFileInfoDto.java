package com.dev.aes.payloads.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParserAnnotatorFileInfoDto {
    private String filelocation;
    private String initialdoctype;
    private String mldoctype;
    private String filename;
    private String filetype;
}
