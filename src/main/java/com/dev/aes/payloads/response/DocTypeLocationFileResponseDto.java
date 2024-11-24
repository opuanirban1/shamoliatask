package com.dev.aes.payloads.response;

import com.dev.aes.entity.DocFile;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocTypeLocationFileResponseDto {
    String location;
    Set<DocFile> docFiles;
}
