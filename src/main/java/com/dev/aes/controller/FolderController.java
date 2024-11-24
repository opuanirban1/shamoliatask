package com.dev.aes.controller;

import com.dev.aes.entity.Folder;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.FolderRootPageRequest;
import com.dev.aes.payloads.request.FolderSubPageRequest;
import com.dev.aes.payloads.request.MoveDto;
import com.dev.aes.payloads.request.SubFolderDto;
import com.dev.aes.payloads.response.*;
import com.dev.aes.service.FolderService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/folders")
public class FolderController {
    private final Logger LOG = LoggerFactory.getLogger(FolderController.class);
    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    @PostMapping("/sub")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<Folder> createFolder(@Valid @RequestBody SubFolderDto dto) {
        log.info("API end point: /api/v1/folder/sub and extension: input {} ", dto);
        return new ResponseEntity<>(folderService.createSubFolder(dto), HttpStatus.CREATED);
    }

    //old
    //@PostMapping("/root")
    @GetMapping("/root")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<FolderResponseDto> getRootFolders() {
        //LOG.info("API end point /api/v1/root and input {}",folderRootPageRequest);
        return new ResponseEntity<>(folderService.getRootFolder(), HttpStatus.OK);
    }

    @PostMapping("/root")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<FolderResponseDto> getRootFoldersPagination(@Valid @RequestBody FolderSubPageRequest folderSubPageRequest) {
        LOG.info("API end point /api/v1/root and input {}",folderSubPageRequest);
        return new ResponseEntity<>(folderService.getRootFolderPagination(folderSubPageRequest), HttpStatus.OK);
    }


    @GetMapping("/nextfileidrootfolder/{doctype}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<NextFileIdByDoctypeSubFolderRes> getNextFileIdRootFOlder( @PathVariable("doctype") String doctype) {
        LOG.info("API end point /api/v1/nextfileidsubfolder  and doctype {}",  doctype.toUpperCase());
        return new ResponseEntity<>(folderService.getNextFileIdBtDOctypeRootFolderDo(doctype), HttpStatus.OK);

    }

    @PostMapping("/sub/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<FolderResponseDto> getSubFoldersWithPagination(@Valid @RequestBody FolderSubPageRequest folderSubPageRequest, @PathVariable("id") Long id) {
        LOG.info("API end point /api/v1/sub and input  {} and id {}",folderSubPageRequest, id);
        return new ResponseEntity<>(folderService.getSubFolderWithPagination(id, folderSubPageRequest), HttpStatus.OK);

    }


    @GetMapping("/nextfileidsubfolder/{id}/{doctype}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<NextFileIdByDoctypeSubFolderRes> getNextFileIdSubFOlder( @PathVariable("id") Integer id, @PathVariable("doctype") String doctype) {
        LOG.info("API end point /api/v1/nextfileidsubfolder and input id {} and doctype {}", id, doctype.toUpperCase());
        return new ResponseEntity<>(folderService.getNextFileIdBtDOctypeSubFolderDo(doctype,id), HttpStatus.OK);

    }

    //old
    @GetMapping("/sub/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<FolderResponseDto> getSubFolders( @PathVariable("id") Long id) {

        //LOG.info("API end point /api/v1/sub and input  {} and id {}",folderSubPageRequest, id);
        return new ResponseEntity<>(folderService.getSubFolder(id), HttpStatus.OK);

    }

    @GetMapping("/doctype/{docType}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocTypeFilesResponseDto> getDoctypeFiles(@PathVariable String docType, @RequestParam List<String> status) {

        LOG.info("API end point /api/v1/doctype/{} and input {}", docType, status);
        return new ResponseEntity<>(folderService.getDocTypeFiles(docType.toUpperCase(), status), HttpStatus.OK);

    }

    @GetMapping("/files/{folderId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<FileResponseDto>> searchFileByStatus(@PathVariable Long folderId,
                                                                    @RequestParam String status) {
        return new ResponseEntity<>(folderService.searchFileByStatus(folderId, status.toUpperCase()), HttpStatus.OK);
    }

    @GetMapping("/doctypeandfilestatus/{docType}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<FileResponseDto>> getDoctypeAndFileStatus(@PathVariable String docType,
                                                                         @RequestParam String status) {
        return new ResponseEntity<>
                (folderService.getDoctypeAndFileStatus(docType.toUpperCase(), status.toUpperCase()), HttpStatus.OK);
    }

    @GetMapping("/doctypeandlocation/{docType}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<DocTypeLocationFileResponseDto>> getDoctypeAndLocationFiles(@PathVariable String docType,
                                                                                           @RequestParam List<String> status) {
        return new ResponseEntity<>(folderService.getDoctypeAndLocationFiles(docType.toUpperCase(), status), HttpStatus.OK);
    }

    @GetMapping("/doctypeandlocation/folderid/{folderId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<DocTypeLocationFileResponseDto>> getByFolderIdAndStatusWithLocation(@PathVariable Long folderId,
                                                                                                   @RequestParam List<String> status) {
        return new ResponseEntity<>(folderService.getByFolderIdAndStatusWithLocation(folderId, status), HttpStatus.OK);
    }

    @GetMapping("/parentFolderList/{folderId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<ParentFolderResponseDto>> getByFolderIdAndStatusWithLocation(@PathVariable Long folderId) {
        return new ResponseEntity<>(folderService.getParentFolderList(folderId), HttpStatus.OK);
    }

    @DeleteMapping("/{folderId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long folderId) {
        return new ResponseEntity<>(folderService.delete(folderId), HttpStatus.OK);
    }

    @PutMapping("/move")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> moveTo(@RequestBody MoveDto moveDto) {
        try {
            LOG.info("API endpoints /api/v1/folders/move and body {} ", moveDto);
            return new ResponseEntity<>(folderService.moveTo(moveDto), HttpStatus.OK);
        }catch (Exception ex){
            throw new OcrDmsException("Element already exist");
        }
    }

    @GetMapping("/global-search")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> search(@RequestParam(value = "", required=false) String searchString,
                                    @RequestParam Long folderId,
                                    @RequestParam String searchType) {

        return new ResponseEntity<>(folderService.localGlobalSearch(searchString, folderId, searchType), HttpStatus.OK);
    }
}
