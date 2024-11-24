package com.dev.aes.service;

import com.dev.aes.payloads.request.FolderShareCreateDto;
import com.dev.aes.payloads.request.FolderSubPageRequest;
import com.dev.aes.payloads.request.RevokeUsrPermissionReqDto;
import com.dev.aes.payloads.response.FolderResponseDto;
import com.dev.aes.payloads.response.RevokeUsrPermResDto;

import java.util.List;

public interface FolderShareService {
    String create(FolderShareCreateDto dto);

    List<FolderResponseDto> getFolderShareWithUser();

    List<Integer>  getUserIdFromFolderId(String folderid);

    RevokeUsrPermResDto doRevokeUserPermission (RevokeUsrPermissionReqDto revokeUsrPermissionReqDto);

    List<FolderResponseDto> getFolderShareWithUser(FolderSubPageRequest folderSubPageRequest);

    FolderResponseDto getFolderShareWithUserWithSubFolderId(Integer subfolderid,FolderSubPageRequest folderSubPageRequest);

    }
