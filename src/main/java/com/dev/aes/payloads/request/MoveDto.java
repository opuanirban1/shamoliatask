package com.dev.aes.payloads.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MoveDto {
    private List<Long> folderIds;
    private List<Long> fileIds;
    private Long destinationFolderId;
}
