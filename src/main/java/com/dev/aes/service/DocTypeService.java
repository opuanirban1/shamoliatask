package com.dev.aes.service;

import com.dev.aes.entity.*;
import com.dev.aes.payloads.request.*;
import com.dev.aes.payloads.response.*;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface DocTypeService {
    void saveAllDocType(List<DocTypeResponseDto> allDocType);

    List<DocType> getAllDocType();

    DocType findDocTypeByName(String docType);

    DocType findDocTypeByNameForBulkOcr(String docType);

    DocType createFile(MultipartFile file, String docType);

    ResponseEntity<Object> getFile(String docType);

    List<DocTypeField> getDocTypeConfig(String docType);

    List<AnnotatorViewDoctypeFieldByDocIDRes> getDocTypeFieldBydoctypeidUserName (Integer doctypeid);

    List<DocTypeUserWise> getUserWiseActiveDoctype();

    AnnoViewDocTypeCreationRes createDocTypeMainClasseqByUser(DocTypeCreateMainClassDto dto);

    AnnoViewDocTypeCreationRes  createDocTypeSubClasseqByUser(DocTypeCreateSubClassDto dto);

    AnnoViewDocTypeFieldCreationRes createDocTypeFieldReqByUser(DocTypeFieldCreateDto dto, Long docTypeId, Long docTypePageId) throws Exception;

    DocType createFileByuser(MultipartFile file, String docTypeName,Long createBy);

    //DocType doApproveRejectBydoctypeIduser(ApproveRejectRequest approveRejectRequest);

    DocTypeApproveRejectResponse doApproveRejectBydoctypeIduser(ApproveRejectRequest approveRejectRequest);

    ImportDocTypeUserwiseResponse doImportDocTypeUserwise(ImportDocTypeUserwiseRequest importDocTypeUserwiseRequest);
}
