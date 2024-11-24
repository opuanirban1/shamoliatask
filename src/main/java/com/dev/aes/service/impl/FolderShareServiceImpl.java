package com.dev.aes.service.impl;

import com.dev.aes.entity.FolderShare;
import com.dev.aes.entity.User;
import com.dev.aes.payloads.request.FolderShareCreateDto;
import com.dev.aes.payloads.request.FolderSubPageRequest;
import com.dev.aes.payloads.request.RevokeUsrPermissionReqDto;
import com.dev.aes.payloads.response.FolderResponseDto;
import com.dev.aes.payloads.response.RevokeUsrPermResDto;
import com.dev.aes.repository.FileRepository;
import com.dev.aes.repository.FolderRepository;
import com.dev.aes.repository.FolderShareRepository;
import com.dev.aes.service.FolderService;
import com.dev.aes.service.FolderShareService;
import com.dev.aes.service.UserService;
import com.dev.aes.util.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FolderShareServiceImpl implements FolderShareService {
    private final FolderShareRepository repository;
    private final UserService userService;
    private final FolderService folderService;

    @Autowired
    Pagination pagination;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    public FolderShareServiceImpl(FolderShareRepository repository,
                                  UserService userService,
                                  FolderService folderService) {
        this.repository = repository;
        this.userService = userService;
        this.folderService = folderService;
    }

    @Override
    public String create(FolderShareCreateDto dto) {
        if(Objects.isNull(dto.getUserIds()) || dto.getUserIds().isEmpty()){
            return "Successfully Saved";
        }
        dto.getUserIds().forEach(id ->
                repository.save(FolderShare.builder().folderId(dto.getFolderId()).userId(id).build()));
        return "Successfully Saved";
    }

    @Override
    public List<FolderResponseDto> getFolderShareWithUser() {
        User currentuser = userService.getCurrentuser();
        List<FolderShare> folderShares = repository.findAllByUserId(currentuser.getId());
        if (folderShares.isEmpty()){
            return List.of();
        }

        List<FolderResponseDto> folderResponseDtos = new ArrayList<>();
        for (FolderShare folderShare : folderShares){
            FolderResponseDto subFolder = folderService.getSubFolder(folderShare.getFolderId());
            if (Objects.nonNull(subFolder)){
                folderResponseDtos.add(subFolder);
            }
        }
        return folderResponseDtos;
    }

    @Override
    public List<Integer>  getUserIdFromFolderId(String folderid){
        return repository.findUserIdByFolderId(Long.parseLong(folderid));
    }

   /* @Override
    public RevokeUsrPermResDto doRevokeUserPermission (RevokeUsrPermissionReqDto revokeUsrPermissionReqDto){
        if (revokeUsrPermissionReqDto.getUserid().isEmpty()) {
            return null;
        }

        RevokeUsrPermResDto revokeUsrPermResDto = new RevokeUsrPermResDto();
        repository.doRevokeUserIdByfolderId(revokeUsrPermissionReqDto.getFolderid(),revokeUsrPermissionReqDto.getUserid());

        // change files user creator
        User user = userService.getCurrentuser();
        fileRepository.updateDocfilesByCreatedbyandFolderId( user.getId(), revokeUsrPermissionReqDto.getFolderid(), revokeUsrPermissionReqDto.getUserid());

        revokeUsrPermResDto.setMessage("User revoked by folder id");
        revokeUsrPermResDto.setStatus("success");
        return revokeUsrPermResDto;
    }*/



    @Override
    public RevokeUsrPermResDto doRevokeUserPermission (RevokeUsrPermissionReqDto revokeUsrPermissionReqDto){
        if (revokeUsrPermissionReqDto.getUserid().isEmpty()) {
            return null;
        }

        RevokeUsrPermResDto revokeUsrPermResDto = new RevokeUsrPermResDto();
        repository.doRevokeUserIdByfolderId(revokeUsrPermissionReqDto.getFolderid(),revokeUsrPermissionReqDto.getUserid());

        // change files user creator
        User user = userService.getCurrentuser();
        List<Long> getSubFolderIDByFolderID  = folderRepository.getSubFolderIdsByFolderId(Long.parseLong(revokeUsrPermissionReqDto.getFolderid().toString()));

        //fileRepository.updateDocfilesByCreatedbyandFolderId( user.getId(), revokeUsrPermissionReqDto.getFolderid(), revokeUsrPermissionReqDto.getUserid());
        fileRepository.updateDocfilesByCreatedbyandFolderId( user.getId(),getSubFolderIDByFolderID, revokeUsrPermissionReqDto.getUserid());


        revokeUsrPermResDto.setMessage("User revoked by folder id");
        revokeUsrPermResDto.setStatus("success");
        return revokeUsrPermResDto;
    }

    @Override
    public List<FolderResponseDto> getFolderShareWithUser(FolderSubPageRequest folderSubPageRequest) {

        Integer totalrowcount=0,startlimit=0,endlimit=0, totalpage=0;
        User currentuser = userService.getCurrentuser();
        System.out.println("User folder Anirban "+currentuser.getId());
        List<FolderShare> folderShares = new ArrayList<FolderShare>();
        List<FolderResponseDto> folderResponseDtos = new ArrayList<>();
        //List<FolderShare> folderShares = repository.findAllByUserId(currentuser.getId());
        //Pagination start
        totalrowcount =  repository.findAllByUserIDWithPaginationCount(currentuser.getId());
        startlimit = pagination.getstartlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
        endlimit = pagination.getendlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
        totalpage = pagination.gettotalnoofpage(totalrowcount, folderSubPageRequest.getLimitperpage());
        folderShares  = repository.findAllByUserIDWithPaginationData( startlimit,  endlimit , currentuser.getId());
        // Pagination end
        if (folderShares.isEmpty()){
            return List.of();
        }
        for (FolderShare folderShare : folderShares){
            FolderResponseDto subFolder = folderService.getSubFolderWithPagination(folderShare.getFolderId(), folderSubPageRequest);
            subFolder.setTotalpageMain( totalpage);
            subFolder.setTotalrow( totalrowcount);
            subFolder.setPageno(folderSubPageRequest.getPageno());
            if (Objects.nonNull(subFolder)){
                folderResponseDtos.add(subFolder);
            }
        }
        return folderResponseDtos;
    }

    @Override
    public FolderResponseDto getFolderShareWithUserWithSubFolderId(Integer subfolderid,FolderSubPageRequest folderSubPageRequest) {

        Integer totalrowcount=0,startlimit=0,endlimit=0, totalpage=0;
        User currentuser = userService.getCurrentuser();
        System.out.println("User folder Anirban "+currentuser.getId());
        FolderShare  folderShare = new FolderShare();
        FolderResponseDto folderResponseDto = new FolderResponseDto();
        //List<FolderShare> folderShares = new ArrayList<FolderShare>();
        List<FolderResponseDto> folderResponseDtos = new ArrayList<>();
        //List<FolderShare> folderShares = repository.findAllByUserId(currentuser.getId());
        //Pagination start
        //totalrowcount =  repository.findAllByUserIDWithPaginationCount(currentuser.getId());
        //startlimit = pagination.getstartlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
        //endlimit = pagination.getendlimit(folderSubPageRequest.getLimitperpage(), folderSubPageRequest.getPageno());
        //totalpage = pagination.gettotalnoofpage(totalrowcount, folderSubPageRequest.getLimitperpage());
        folderShare  = repository.getFoldershareById(subfolderid);
        // Pagination end
        //if (folderShares.isEmpty()){
        //  return List.of();
        //}
        //for (FolderShare folderShare : folderShares){
        folderResponseDto = folderService.getSubFolderWithPagination(subfolderid.longValue(), folderSubPageRequest);
        //subFolder.setTotalpageMain( totalpage);
        // subFolder.setTotalrow( totalrowcount);
        //subFolder.setPageno(folderSubPageRequest.getPageno());
        //  if (Objects.nonNull(subFolder)){
        //    folderResponseDtos.add(subFolder);
        //}
        //}
        return folderResponseDto;
    }



}
