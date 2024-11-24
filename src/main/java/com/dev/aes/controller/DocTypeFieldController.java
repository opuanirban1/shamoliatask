package com.dev.aes.controller;

import com.dev.aes.entity.DocTypeField;
import com.dev.aes.entity.DocTypeFieldusername;
import com.dev.aes.entity.FileContentField;
import com.dev.aes.payloads.request.EnterDataRequest;
import com.dev.aes.payloads.request.LanguageRequestDto;
import com.dev.aes.payloads.response.EnterDataResponse;
import com.dev.aes.service.DocTypeFieldService;
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
@RequestMapping("/api/v1/docTypeField")
public class DocTypeFieldController {
    private final Logger LOG = LoggerFactory.getLogger(DocTypeFieldController.class);
    private final DocTypeFieldService service;
    @Autowired
    public DocTypeFieldController(DocTypeFieldService service) {
        this.service = service;
    }

   /* @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocTypeField> uploadFile(@Valid @RequestBody LanguageRequestDto dto,@PathVariable Long id) {
        return new ResponseEntity<>(service.updateLanguage(dto, id), HttpStatus.CREATED);
    }*/

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocTypeFieldusername> setLanuage(@Valid @RequestBody LanguageRequestDto dto, @PathVariable Long id) {
        return new ResponseEntity<>(service.updateLanguageUsername(dto, id), HttpStatus.CREATED);
    }

    @GetMapping("/{doctypeid}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getAmardocDocTypefield(@PathVariable Long doctypeid) {
        LOG.info("API end point {}/getDocTypeField/{}","/api/v1/docTypeField",doctypeid);
        return new ResponseEntity<>(service.findAllByDocTypeId(doctypeid), HttpStatus.OK);
        //return new ResponseEntity<>("Hello", HttpStatus.OK);
    }


    @PostMapping("/insertUpdate")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<EnterDataResponse> insertUpdateFieldValue(@Valid @RequestBody EnterDataRequest enterDataRequest){
        LOG.info("API end point /api/v1/docTypeField/insertUpdate and input {}",enterDataRequest);
        return new ResponseEntity<>(service.insertUpdateDoctypeField(enterDataRequest), HttpStatus.OK);
    }


    @GetMapping("/showEnterDataByFileId/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    @ResponseBody
    public ResponseEntity<List<FileContentField>> getFileContentData(@PathVariable Long id){
        LOG.info("API end point /api/v1/docTypeField/showEnterDataByFileId/{} ",id);
        return new ResponseEntity<>(service.getFileContentDataByFileId(id), HttpStatus.OK);
    }

}
