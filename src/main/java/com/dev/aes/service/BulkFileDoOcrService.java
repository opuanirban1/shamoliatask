package com.dev.aes.service;

import com.dev.aes.payloads.request.FileIds;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BulkFileDoOcrService {

    void doBulkFileOcr() throws Exception;

    String create(FileIds fileIds);

    void doBulkDocTypeDetection() throws Exception;

    String createOCR(FileIds fileIds) throws  Exception;

    void doBulkFileOcrAuth () throws  Exception;

    void doBulkDocTypeDetectionAuth() throws Exception;

    void doErrorDetection() throws Exception;

    //void setFileStatusBulkErrorEnqueue(List<Long> ids);
}
