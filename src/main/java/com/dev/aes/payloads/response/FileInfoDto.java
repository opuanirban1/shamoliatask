package com.dev.aes.payloads.response;

import com.dev.aes.entity.FileContentField;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfoDto {
    private Long id;
    private String fileName;
    private String fileType;
    private String docType;
    private String location;
    private String filePath;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FileContentField> fileContentFieldList;
}
