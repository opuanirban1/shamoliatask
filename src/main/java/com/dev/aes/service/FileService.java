package com.dev.aes.service;

import com.dev.aes.entity.DocFile;
import com.dev.aes.entity.FileContentField;
import com.dev.aes.payloads.request.ConfirmOCRFinishDoctypeListReq;
import com.dev.aes.payloads.request.ConfirmStatusDoctypeListReq;
import com.dev.aes.payloads.request.FileUpdateDto;
import com.dev.aes.payloads.response.FileInfoDto;
import com.dev.aes.payloads.response.ReRunResponse;
import com.dev.aes.payloads.response.SearchResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.List;

public interface FileService {

    DocFile createFile(MultipartFile file, Long folderId) throws Exception;

    ResponseEntity<Object> getFile(Long id);

    DocFile getFileById(Long id);

    List<FileContentField> runOcr(Long id) throws ParseException;

    List<Long> getFileIdListByStatus(Long folderId, String status);

    FileInfoDto getFileInfo(Long id) throws ParseException;

    void doBulkOcr(DocFile docFile);

    String updateFile(FileUpdateDto fileUpdateDto);

    List<DocFile> findByDocType(String notDetected, Integer docDetectionLimit);

    void doBulkDocTypeDetection(List<DocFile> docFiles) throws Exception;

    DocFile detectDocType(DocFile docFile) throws Exception;

    void setFileStatusBulkEnqueue(List<Long> ids);

    Long getAverageOcrTime();

    DocFile fileClassifierConfirmation(Long id, String docType);

    String delete(Long id);

    List<SearchResponseDto> search(String value, List<Long> userIds);

    ReRunResponse doReRunOCR(Integer fileid, String doctype, Integer doctypeid) throws ParseException;

    List<FileContentField> runOcrWithDoctypeAndID(Long id, String doctype, Integer doctypid) throws ParseException;

    List<Long> getFileIdListByStatusDoctypeList(Long folderId, String status, ConfirmOCRFinishDoctypeListReq confirmOCRFinishDoctypeListReq);

    void setFileStatusBulkErrorEnqueue(List<Long> ids);

    void doBulkErrorDetection(DocFile docFile) throws ParseException;

     List<Long> getFileIdListByStatusDoctypeListByStatus(Long folderId, String status, ConfirmStatusDoctypeListReq confirmStatusDoctypeListReq);

    String getFileExtensionMod (String name);

}
