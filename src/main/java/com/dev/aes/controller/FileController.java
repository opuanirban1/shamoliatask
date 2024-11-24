package com.dev.aes.controller;

import com.dev.aes.entity.DocFile;
import com.dev.aes.entity.File;
import com.dev.aes.entity.FileContentField;
import com.dev.aes.payloads.request.ConfirmOCRFinishDoctypeListReq;
import com.dev.aes.payloads.request.ConfirmStatusDoctypeListReq;
import com.dev.aes.payloads.request.FileUpdateDto;
import com.dev.aes.payloads.response.FileInfoDto;
import com.dev.aes.payloads.response.ReRunResponse;
import com.dev.aes.service.FileService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final Logger LOG = LoggerFactory.getLogger(FileController.class);
    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload/{folderId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocFile> uploadFile(@RequestParam("file") MultipartFile file,
                                              @PathVariable("folderId") Long folderId, Authentication authentication) throws Exception {
        LOG.info("API endpoint /api/v1/files/upload/folderid and id {}", folderId);
        return new ResponseEntity<>(fileService.createFile(file, folderId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<Object> getFiles(@PathVariable Long id) {
        return fileService.getFile(id);
    }

    //old
    @GetMapping("/ocrrun/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<FileContentField>> runOcr(@PathVariable Long id) throws ParseException {
        return new ResponseEntity<>(fileService.runOcr(id), HttpStatus.OK);
    }

    @GetMapping("/ocrruncheck/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<FileContentField>> runOcrNew(@PathVariable Long id) throws ParseException {
        return new ResponseEntity<>(fileService.runOcr(id), HttpStatus.OK);
    }

    @GetMapping("/ocrrun/{doctype}/{doctypeid}/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<FileContentField>> runOcrNew1(@PathVariable String doctype, @PathVariable Integer doctypeid, @PathVariable Long id) throws ParseException {
        //return new ResponseEntity<>(fileService.runOcr(id), HttpStatus.OK);
        return new ResponseEntity<>(fileService.runOcrWithDoctypeAndID(id, doctype, doctypeid), HttpStatus.OK);
    }

    @GetMapping("/info/{fileId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<FileInfoDto> getInfo(@PathVariable Long fileId) throws ParseException {
        return new ResponseEntity<>(fileService.getFileInfo(fileId), HttpStatus.OK);
    }

    @GetMapping("/averageOcrTime")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<Long> getInfo() {
        return new ResponseEntity<>(fileService.getAverageOcrTime(), HttpStatus.OK);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<String> uploadFile(@RequestBody FileUpdateDto fileUpdateDto) {
        return new ResponseEntity<>(fileService.updateFile(fileUpdateDto), HttpStatus.CREATED);
    }

    @GetMapping("/parser/{id}")
    public ResponseEntity<Object> getFilesParser(@PathVariable Long id) {
        return fileService.getFile(id);
    }

    @GetMapping("/not-confirm/{folderId}")
    public ResponseEntity<Object> getNotConfirmFileIdList(@PathVariable Long folderId) {
        return new ResponseEntity<>(fileService.getFileIdListByStatus(folderId, "NEW"), HttpStatus.OK);
    }
    @GetMapping("/ocr-finished/{folderId}")
    public ResponseEntity<Object> getOcrFinishedFileIdList(@PathVariable Long folderId) {
        return new ResponseEntity<>(fileService.getFileIdListByStatus(folderId, "OCR FINISHED"), HttpStatus.OK);
    }


    @PostMapping("/not-confirm/{folderId}")
    public ResponseEntity<Object> getNotConfirmFileIdListByDoctype(@Valid @RequestBody ConfirmOCRFinishDoctypeListReq confirmOCRFinishDoctypeListReq, @PathVariable Long folderId) {
        return new ResponseEntity<>(fileService.getFileIdListByStatusDoctypeList(folderId, "NEW", confirmOCRFinishDoctypeListReq), HttpStatus.OK);
    }
    @PostMapping("/ocr-finished/{folderId}")
    public ResponseEntity<Object> getOcrFinishedFileIdListByDoctype(@Valid @RequestBody ConfirmOCRFinishDoctypeListReq confirmOCRFinishDoctypeListReq, @PathVariable Long folderId) {
        return new ResponseEntity<>(fileService.getFileIdListByStatusDoctypeList(folderId, "OCR FINISHED", confirmOCRFinishDoctypeListReq), HttpStatus.OK);
    }

    @PostMapping("/by-status/{folderId}")
    public ResponseEntity<Object> getOcrFinishedFileIdListByDoctype(@Valid @RequestBody ConfirmStatusDoctypeListReq confirmStatusDoctypeListReq, @PathVariable Long folderId) {
        return new ResponseEntity<>(fileService.getFileIdListByStatusDoctypeListByStatus(folderId, confirmStatusDoctypeListReq.getStatus().toUpperCase(), confirmStatusDoctypeListReq), HttpStatus.OK);
    }


    @GetMapping("/confirmed/{folderId}")
    public ResponseEntity<Object> getConfirmedFileIdList(@PathVariable Long folderId) {
        return new ResponseEntity<>(fileService.getFileIdListByStatus(folderId, "CONFIRM"), HttpStatus.OK);
    }

    @PutMapping("/confirmation/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocFile> fileClassifierConfirmation(@PathVariable Long id, @RequestParam String docType) {
        LOG.info("API endpoint /confirmation/{}?{} ", id,docType);
        return new ResponseEntity<>(fileService.fileClassifierConfirmation(id, docType), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN')")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        return new ResponseEntity<>(fileService.delete(id), HttpStatus.OK);
    }

    @PostMapping("/reRun/{doctype}/{doctypeid}/{fileid}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<ReRunResponse> doRerunOCR (@PathVariable String doctype, @PathVariable Integer doctypeid ,@PathVariable Integer fileid) throws Exception{

        LOG.info("API endpoint {} and input doctype {} fileid {} doctypeid {}", "/api/v1/files/{doctype}/{doctypeid}/{fileid}", doctype, fileid, doctypeid);
        return new ResponseEntity<>(fileService.doReRunOCR(fileid, doctype, doctypeid), HttpStatus.OK);
    }
}
