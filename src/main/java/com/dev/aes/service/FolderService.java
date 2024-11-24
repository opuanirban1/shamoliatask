package com.dev.aes.service;

import com.dev.aes.entity.Folder;
import com.dev.aes.entity.User;
import com.dev.aes.payloads.request.FolderSubPageRequest;
import com.dev.aes.payloads.request.MoveDto;
import com.dev.aes.payloads.request.SubFolderDto;
import com.dev.aes.payloads.response.*;

import java.util.List;
import java.util.Set;

public interface FolderService {
    Folder createRootFolder(User user);

    Folder createSubFolder(SubFolderDto dto);

    Folder findById(Long folderId);

    FolderResponseDto getRootFolder();

    FolderResponseDto getSubFolder(Long id);

    DocTypeFilesResponseDto getDocTypeFiles(String docType, List<String> status);

    List<FileResponseDto> searchFileByStatus(Long folderId, String upperCase);

    List<FileResponseDto> getDoctypeAndFileStatus(String docType, String status);

    List<DocTypeLocationFileResponseDto> getDoctypeAndLocationFiles(String docType, List<String> status);

    List<DocTypeLocationFileResponseDto> getByFolderIdAndStatusWithLocation(Long folderId, List<String> status);

    List<ParentFolderResponseDto> getParentFolderList(Long folderId);

    String delete(Long folderId);

    String moveTo(MoveDto moveDto);

    List<GlobalSearchDto> localGlobalSearch(String searchString, Long folderId, String searchType);

    List<SearchResponseDto> search(String value, List<Long> userIds);

    List<Folder> getFoldersByParentFolderId(Long folderId);

    FolderResponseDto getRootFolderPagination(FolderSubPageRequest folderSubPageRequest);

    NextFileIdByDoctypeSubFolderRes  getNextFileIdBtDOctypeRootFolderDo(String doctype);

    FolderResponseDto getSubFolderWithPagination(Long id, FolderSubPageRequest folderSubPageRequest);

    NextFileIdByDoctypeSubFolderRes   getNextFileIdBtDOctypeSubFolderDo(String doctype, Integer id);
}
