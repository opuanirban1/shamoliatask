package com.dev.aes.service;

import com.dev.aes.entity.DocFile;
import com.dev.aes.entity.DocType;
import com.dev.aes.entity.FileContentField;
import com.dev.aes.payloads.request.FileContentFieldUpdateDto;
import com.dev.aes.payloads.response.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface FileContentFieldService {

    List<FileContentField> getFieldContentFieldByFile(DocFile docFile) throws ParseException;

    void update(FileContentFieldUpdateDto fileContentFieldUpdateDto);

    void save(Map<String, String> map, DocFile docFile, DocType docType);

    List<SearchResponseDto> search(String value, List<Long> userIds);

    void saveReRunOcr(Map<String, String> map, Integer  doctypeid, Integer fileid);

    void saveRunOcr(Map<String, String> map, Integer  doctypeid, Integer fileid);
}
