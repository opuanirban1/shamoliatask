package com.dev.aes.payloads.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FolderShareCreateDto {
    @NotNull
    Long folderId;
    List<Long> userIds;
}
