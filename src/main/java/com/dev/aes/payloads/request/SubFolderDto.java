package com.dev.aes.payloads.request;

import com.dev.aes.constant.FolderActiveStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubFolderDto {
    @Size(max = 1000)
    private String name;
    private FolderActiveStatus active;
    @NotNull
    private Long parentId;
}
