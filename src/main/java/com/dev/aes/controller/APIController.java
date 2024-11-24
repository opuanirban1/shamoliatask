package com.dev.aes.controller;


import com.dev.aes.entity.DocType;
import com.dev.aes.payloads.request.DocTypeAll;
import com.dev.aes.payloads.response.DocTypeConfigData;
import com.dev.aes.repository.DocTypeRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class APIController {


    private final Logger LOG = LoggerFactory.getLogger(APIController.class);

    @Autowired
    DocTypeRepository docTypeRepository;

    @GetMapping("/test")
    public String getProLanguageDo(@RequestParam(name = "extension", required = false) String extension, HttpServletRequest httpServletRequest) throws ParseException {

        LOG.info("API end point: /api/v1/test and extension: input {} ", extension);

        String res = "hello ocrdms";

        LOG.info("API response {}", res);

        return res;
    }
   /* @GetMapping(value="/getalldoctype")
    public ResponseEntity<List<DocTypeAll>> getAllDoctype() throws Exception {
        LOG.info("API  /getAllDoctype  ");
        List<DocTypeAll> docTypeAlls = new ArrayList<DocTypeAll>();
        List<DocType> docTypeAllList = new ArrayList<>();
        docTypeAllList = docTypeRepository.getAllDoctype();
        for (Integer i=0; i<docTypeAllList.size(); i++){
            DocTypeAll docTypeAll = new DocTypeAll(docTypeAllList .get(i).getId().toString(), docTypeAllList.get(i).getName());
            docTypeAlls.add(docTypeAll);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(docTypeAlls);

    }*/

 /*   @GetMapping(value="/getdocconfigdatabydoctype"
    public ResponseEntity<List<DocTypeConfigData>>  getDocConfigDataByDoctype (@RequestParam("doctype") String doctype) throws Exception {

        String parseResponse="";
        LOG.info("API  /getDocConfigDataByDoctype and doctype {}", doctype);

        //parseResponse = docParserService.doParsingByDoc("uploads/", file.getContentType(), file, doctype.replaceAll("[^a-zA-Z]",""));

        //LOG.info("API Resposne  {}", parseResponse);

        //DocTypeConfigAllResponse  docTypeConfigAllResponse = new DocTypeConfigAllResponse();
        List<DocTypeConfigData> docTypeConfigData = new ArrayList<>();

        docTypeConfigData = docConfigService.getDocConfigDataByDocType(doctype.toUpperCase());

        //docTypeConfigAllResponse.setInfo(docTypeConfigData);

        //return ResponseEntity.status(HttpStatus.OK).body(nidParseResponseDto.);
        //return parseResponse.toString();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(docTypeConfigData );

    }*/


}
