package com.dev.aes.controller;

import com.dev.aes.config.APIGatewayManager;
import com.dev.aes.entity.*;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.*;
import com.dev.aes.payloads.response.AnnoViewDocTypeCreationRes;
import com.dev.aes.payloads.response.AnnoViewDocTypeFieldCreationRes;
import com.dev.aes.payloads.response.AnnotatorViewDoctypeFieldByDocIDRes;
import com.dev.aes.payloads.response.DocTypeApproveRejectResponse;
import com.dev.aes.repository.DocTypeRepository;
import com.dev.aes.service.DocTypeFieldService;
import com.dev.aes.service.DocTypeService;
import com.dev.aes.service.FileService;
import com.dev.aes.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/docType")
public class DocTypeController {
    private final DocTypeService docTypeService;
    private final DocTypeFieldService docTypeFieldService;
    private final APIGatewayManager apiGatewayManager;
    private final UserService userService;

    @Autowired
    DocTypeRepository docTypeRepository;

    @Autowired
    FileService fileService;

    @Autowired
    public DocTypeController(DocTypeService docTypeService, DocTypeFieldService docTypeFieldService, APIGatewayManager apiGatewayManager, UserService userService) {
        this.docTypeService = docTypeService;
        this.docTypeFieldService = docTypeFieldService;
        this.apiGatewayManager = apiGatewayManager;
        this.userService = userService;
    }

    /*  @GetMapping("/all")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getAllDocTypes() {
        List<DocTypeResponse> response = new ArrayList<>();
        List<DocType> docTypes = docTypeService.getAllDocType();
        docTypes.forEach(docType -> {
            DocTypeResponse docTypeResponse = new DocTypeResponse();
            docTypeResponse.setDocType(docType);
            docTypeResponse.setPageNumbers(apiGatewayManager.getAllPages(docType.getDocTypeId()));
            response.add(docTypeResponse);
        });
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/field/{docTypeId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getDocTypeFields(@PathVariable Long docTypeId, @RequestParam("pageNumber") Long pageNumber) {
        return new ResponseEntity<>(docTypeFieldService.getDocTypeFields(docTypeId, pageNumber), HttpStatus.OK);
    }*/

    @GetMapping("/config/{docType}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<DocTypeField>> getDocTypeConfig(@PathVariable String docType) {
        return new ResponseEntity<>(docTypeService.getDocTypeConfig(docType), HttpStatus.OK);
    }

    @GetMapping("/userwiseactivedoctype")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<DocTypeUserWise>> getUserWiseActiveDoctype() {
        return new ResponseEntity<>(docTypeService.getUserWiseActiveDoctype(), HttpStatus.OK);
    }

    @PostMapping("/create/mainclass")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<AnnoViewDocTypeCreationRes> createDocTypeMainClass(@Valid @RequestBody DocTypeCreateMainClassDto dto) {
        return new ResponseEntity<>(docTypeService.createDocTypeMainClasseqByUser(dto), HttpStatus.CREATED);
    }


    @PostMapping("/create/subclass")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<AnnoViewDocTypeCreationRes> createDocTypeSubClass(@Valid @RequestBody DocTypeCreateSubClassDto dto) {
        return new ResponseEntity<>(docTypeService.createDocTypeSubClasseqByUser(dto), HttpStatus.CREATED);
    }


    @PostMapping("/createDocTypeField/{docTypeId}/{docTypePageId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<AnnoViewDocTypeFieldCreationRes> createDocTypeField(@Valid @RequestBody DocTypeFieldCreateDto dto,
                                                                              @PathVariable Long docTypeId, @PathVariable Long docTypePageId) throws Exception {
        return new ResponseEntity<>(docTypeService.createDocTypeFieldReqByUser(dto, docTypeId,docTypePageId), HttpStatus.CREATED);
    }


    @PostMapping("/upload/{docType}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocType> uploadFile(@RequestParam("file") MultipartFile file,
                                           @PathVariable("docType") String docType) throws Exception {
        return new ResponseEntity<>(docTypeService.createFile(file, docType), HttpStatus.CREATED);
    }

    @PostMapping("/uploadByUser/{doctypeid}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<AnnoViewDocTypeCreationRes> uploadFileByUser(@RequestParam("file") MultipartFile file,
                                              @PathVariable("doctypeid") Long doctypeid) throws Exception {
        //return new ResponseEntity<>(docTypeService.createFile(file, docType), HttpStatus.CREATED);

        System.out.println("Extension "+fileService.getFileExtensionMod(file.getOriginalFilename()));
        if (fileService.getFileExtensionMod(file.getOriginalFilename()).equalsIgnoreCase(".jpeg") ||
                fileService.getFileExtensionMod(file.getOriginalFilename()).equalsIgnoreCase(".jpg")||
                fileService.getFileExtensionMod(file.getOriginalFilename()).equalsIgnoreCase(".png")||
                fileService.getFileExtensionMod(file.getOriginalFilename()).equalsIgnoreCase(".pdf")) {

            User user = userService.getCurrentuser();
            AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = apiGatewayManager.getDoctypeById(doctypeid);
            DocType docType = docTypeService.createFileByuser(file, annoViewDocTypeCreationRes.getName(), annoViewDocTypeCreationRes.getRequserid());
            String location = docType.getLocation();
            String filename = docType.getFileName();

            return new ResponseEntity<>(apiGatewayManager.uploadDocByUser(doctypeid, user.getId(), location, filename), HttpStatus.OK);
        }else{

            throw  new OcrDmsException("Only pdf,jpg.jpeg,pdf is allowed");
        }
    }

    @GetMapping("/file/{docType}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<Object> getFiles(@PathVariable String docType) {
        return docTypeService.getFile(docType);
    }

    // for annotatorview API
    @GetMapping(value="/allAnnotator", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getAllDocTypesAPI() {
        return new ResponseEntity<>(apiGatewayManager.getAllPages(), HttpStatus.OK);
    }

    @GetMapping(value="/getDoctypePageAnnotator/{doctypeid}", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getgetDoctypePageBydoctypeAPI(@PathVariable Integer doctypeid) {
        return new ResponseEntity<>(apiGatewayManager.getDocTypeSubclassBydoctypeid(doctypeid), HttpStatus.OK);
    }
    @GetMapping(value="/getDoctypeFieldAnntator/{doctypeid}", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getgetDoctypeFieldBydoctypeAPI(@PathVariable Integer doctypeid) {
        //return new ResponseEntity<>(apiGatewayManager.getDocTypeFieldBydoctypeid(doctypeid), HttpStatus.OK);

       return new ResponseEntity<>(docTypeService.getDocTypeFieldBydoctypeidUserName(doctypeid), HttpStatus.OK);
    }

    @GetMapping("/fileAnnotator/{docTypeId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<Object> getFilesAnnotatorAPI(@PathVariable Integer docTypeId) throws IOException {
        //return new ResponseEntity<>(apiGatewayManager.getDoctypeFileByDoctypeId(docTypeId), HttpStatus.OK);
        return apiGatewayManager.getDoctypeFileByDoctypeId(docTypeId);
        //return docTypeService.getFile(docTypeId);

    }

    @GetMapping("/getDoctypeById/{doctypeid}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<AnnoViewDocTypeCreationRes>  getLastDoctypeById (@PathVariable  Long doctypeid){

        return new ResponseEntity<>( apiGatewayManager.getDoctypeById(doctypeid), HttpStatus.OK);

    }// end function



    @GetMapping("/requestedDocTypeByUserId")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<List<AnnoViewDocTypeCreationRes>> getRequestedDocTypeBYUserId() {
        User user = userService.getCurrentuser();
        RequstedDoctypeByUserReq requstedDoctypeByUserReq = new RequstedDoctypeByUserReq();
        //System.out.println("Anirban User id---"+user.getId());
        requstedDoctypeByUserReq.setRequserid(user.getId());
        //System.out.println("anirban input"+ requstedDoctypeByUserReq);
        return new ResponseEntity<>( apiGatewayManager.getRequsetedDoctypeByUserId(requstedDoctypeByUserReq), HttpStatus.OK);

    }// end function


    @GetMapping("/fetchDocTypeFieldsByDocTypeId/{doctypoeid}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?>  getRequsetedDoctypeFieldByUserId (@PathVariable  Long doctypoeid){

        return new ResponseEntity<>( apiGatewayManager.getRequsetedDoctypeFieldByDocId(doctypoeid), HttpStatus.OK);
    }// end function

    //getAllDoctypeFieldTypes()


    @GetMapping("/getAllTypeFieldType")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getAllDoctypeFieldTypes(){

        return new ResponseEntity<>( apiGatewayManager.getAllDoctypeFieldTypes(), HttpStatus.OK);
    }// end


    /*@GetMapping("/getAllTypeFieldType")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getAllDoctypeFieldTypes(){

        return new ResponseEntity<>( apiGatewayManager.getAllDoctypeFieldTypes(), HttpStatus.OK);
    }*/


    @PostMapping("/doApproveReject")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<DocTypeApproveRejectResponse> doApproveReject(@Valid @RequestBody ApproveRejectRequest approveRejectRequest) {

        return new ResponseEntity<>(docTypeService.doApproveRejectBydoctypeIduser(approveRejectRequest), HttpStatus.OK);
    }


    @GetMapping("getDoctypePageByDocTypeId/{doctypeid}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?>  getDoctypePageBydocTypeId(@PathVariable  Long doctypeid){


        return new ResponseEntity<>(apiGatewayManager.getDoctypePage(doctypeid), HttpStatus.OK);

    }


    @PostMapping("/importDocTypeUserwise")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> doImportDocTypeUserwise(@Valid @RequestBody ImportDocTypeUserwiseRequest importDocTypeUserwiseRequest) {

        return new ResponseEntity<>(docTypeService.doImportDocTypeUserwise(importDocTypeUserwiseRequest), HttpStatus.OK);
    }


    @PostMapping("/finalsubmitfromamardoc/{doctypeid}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ANNOTATOR')")
    public ResponseEntity<AnnoViewDocTypeCreationRes> finalsubmitfromamardoc(@PathVariable Long doctypeid) {

        return new ResponseEntity<>(apiGatewayManager.finalSubmitDoctypeIdAmarDoc(doctypeid), HttpStatus.OK);
    }


}
