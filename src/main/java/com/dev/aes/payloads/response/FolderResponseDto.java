package com.dev.aes.payloads.response;

import com.dev.aes.constant.FolderActiveStatus;
import com.dev.aes.constant.FolderType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FolderResponseDto {
   /* private Long id;
    private String name;
    private FolderType type;
    private FolderActiveStatus active;
    private Long unParseCount;
    private Long parsedCount;
    private Long pendingConfirmationCount;
    private Long estimatedTimeInSec;
    Set<SubFolderResponseDto> subFolders;
    Set<FileResponseDto> files;*/
    private Long id;
    private String name;
    private FolderType type;
    private FolderActiveStatus active;
    private Long unParseCount;
    private Long parsedCount;
    private Long pendingConfirmationCount;
    private Long estimatedTimeInSec;
    private Integer pageno;
    private Integer totalpage;
    private Integer totalrow;
    private Integer limitperpage;
    private Integer totalpageFolder;
    private Integer totalrowFolder;
    private Integer totalpageMain;
    private Integer totalrowMain;
    private Integer errorcount;
    Set<SubFolderResponseDto> subFolders;
    //Set<FileResponseDto> files;
    List<FileResponseDto> files;
}
