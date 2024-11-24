package com.dev.aes.payloads.response;

import com.dev.aes.entity.DocFile;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocTypeFilesResponseDto {
    private Long unParseCount;
    private Long parsedCount;
    private Long pendingConfirmationCount;
    private Long estimatedTimeInSec;
    private Set<DocFile> docFiles;
}
