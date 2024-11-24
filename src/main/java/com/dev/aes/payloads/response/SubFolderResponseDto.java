package com.dev.aes.payloads.response;

import com.dev.aes.constant.FolderActiveStatus;
import com.dev.aes.constant.FolderType;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubFolderResponseDto {
    private Long id;
    private String name;
    private FolderType type;
    private FolderActiveStatus active;
    private Long parentId;
    private Integer subFolderCount;
    private List<FolderShareUserResponseDto> shareUsers;
}
