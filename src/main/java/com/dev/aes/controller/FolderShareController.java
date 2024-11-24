package com.dev.aes.controller;

import com.dev.aes.payloads.request.FolderShareCreateDto;
import com.dev.aes.payloads.request.FolderSharePaginationRequest;
import com.dev.aes.payloads.request.FolderSubPageRequest;
import com.dev.aes.payloads.request.RevokeUsrPermissionReqDto;
import com.dev.aes.payloads.response.FolderResponseDto;
import com.dev.aes.payloads.response.RevokeUsrPermResDto;
import com.dev.aes.service.FolderShareService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/folder/share")
public class FolderShareController {
    private final FolderShareService service;

    @Autowired
    public FolderShareController(FolderShareService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<String> createFolderShare(@Valid @RequestBody FolderShareCreateDto dto){
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }

    @PostMapping("/all")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<FolderResponseDto>> getFolderShare(@Valid @RequestBody FolderSubPageRequest folderSubPageRequest){
        return new ResponseEntity<>(service.getFolderShareWithUser(folderSubPageRequest), HttpStatus.OK);
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<FolderResponseDto> getFolderShareWithSubFOlderId(@Valid @RequestBody FolderSubPageRequest folderSubPageRequest,
                                                                           @PathVariable Integer  id){
        return new ResponseEntity<>(service.getFolderShareWithUserWithSubFolderId(id,folderSubPageRequest), HttpStatus.OK);
    }


    /*
    @GetMapping
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<List<FolderResponseDto>> getFolderShare(){
         return new ResponseEntity<>(service.getFolderShareWithUser(), HttpStatus.OK);
    }
    */

    @GetMapping("/getuseridbyfolderid/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<List<Integer>> getuseridbyfolderid (@PathVariable String id){
        return new ResponseEntity<>(service.getUserIdFromFolderId(id), HttpStatus.OK);
    }

    @PostMapping("/revokeuserpermission")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<RevokeUsrPermResDto> revokeuserpermission (@RequestBody RevokeUsrPermissionReqDto revokeUsrPermissionReqDto){
        return new ResponseEntity<>( service.doRevokeUserPermission(revokeUsrPermissionReqDto), HttpStatus.OK);
    }
}
