package com.dev.aes.payloads.response;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponseDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String docType;
    private String keyFileName;
    private String location;
    private String status;
    private String folderName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
