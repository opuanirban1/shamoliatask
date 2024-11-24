package com.dev.aes.service;

import com.dev.aes.entity.*;
import com.dev.aes.payloads.request.DocTypeFieldCreateDto;
import com.dev.aes.payloads.request.EnterDataRequest;
import com.dev.aes.payloads.request.LanguageRequestDto;
import com.dev.aes.payloads.response.DocTypeResponseDto;
import com.dev.aes.payloads.response.EnterDataResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface DocTypeFieldService {
    void saveDocTypesFields(List<DocType> allDocType);

    List<DocTypeField> findAllByDocTypeId(Long id);
    DocTypeField updateLanguage(LanguageRequestDto dto, Long id);
    List<DocTypeField> getDocTypeFields(Long docTypeId, Long pageNumber);
    public EnterDataResponse insertUpdateDoctypeField (EnterDataRequest enterDataRequest);
    public List<FileContentField> getFileContentDataByFileId (Long id);
    DocTypeFieldusername updateLanguageUsername(LanguageRequestDto dto, Long id);
    DocTypeFieldReqByUser createDocTypeFieldReqByUser(DocTypeFieldCreateDto dto, Long doctypeid, Long docTypePageId) throws Exception;
}
