package com.dev.aes.service.impl;

import com.dev.aes.config.APIGatewayManager;
import com.dev.aes.config.FileStorageProperties;
import com.dev.aes.entity.*;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.*;
import com.dev.aes.payloads.response.*;
import com.dev.aes.repository.*;
import com.dev.aes.service.DocTypeFieldService;
import com.dev.aes.service.DocTypeService;
import com.dev.aes.service.UserService;
import com.google.gson.Gson;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

import static com.dev.aes.service.impl.FileServiceImpl.getFileExtension;

@Service
public class DocTypeServiceImpl implements DocTypeService {
    private final DocTypeRepository docTypeRepository;
    private final APIGatewayManager apiGatewayManager;
    private final DocTypeFieldService docTypeFieldService;
    private final Path root;

    @Autowired
    DocTypeFieldUserNameRepository docTypeFieldUserNameRepository;

    @Autowired
    UserService userService;

    @Autowired
    DocTypeUserWiseRepository docTypeUserWiseRepository;

    @Autowired
    DocTypeReqByUserRepository docTypeReqByUserRepository;

    @Autowired
    DocTypeFieldReqByUserRepository docTypeFieldReqByUserRepository;

    @Autowired
    public DocTypeServiceImpl(DocTypeRepository docTypeRepository,
                              APIGatewayManager apiGatewayManager,
                              DocTypeFieldService docTypeFieldService,
                              FileStorageProperties fileStorageProperties) {
        this.docTypeRepository = docTypeRepository;
        this.apiGatewayManager = apiGatewayManager;
        this.docTypeFieldService = docTypeFieldService;
        this.root = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.root);
        } catch (Exception ex) {
            throw new OcrDmsException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    @Override
    public void saveAllDocType(List<DocTypeResponseDto> allDocType) {
        if (allDocType == null || allDocType.isEmpty()) {
            return;
        }
        for (DocTypeResponseDto docType : allDocType) {
            if(!docTypeRepository.existsByName(docType.getName()) && docType.getSubclass() == null) {
                DocType entity = new DocType();
                if(docType.getMultipagestatus() != null)entity.setIsMultiPageDoc(docType.getMultipagestatus().equals("yes"));
                entity.setName(docType.getName());
                entity.setOcrstatus(entity.getOcrstatus());
                entity.setDocTypeId(docType.getId());
                docTypeRepository.save(entity);
            }
        }
    }

    @Override
    public List<DocType> getAllDocType() {
        return docTypeRepository.findAll();
    }

    @Override
    public DocType findDocTypeByName(String docType) {
        Optional<DocType> byName = docTypeRepository.findByName(docType);
        if (byName.isPresent()) {
            return byName.get();
        }
        else {
            List<DocTypeResponseDto> list = apiGatewayManager.getAllDocType();
            saveAllDocType(list);
            DocType docTypeNotFound = docTypeRepository.findByName(docType)
                    .orElseThrow(() -> new OcrDmsException("DocType not found", HttpStatus.NOT_FOUND));
            docTypeFieldService.saveDocTypesFields(List.of(docTypeNotFound));
            return docTypeNotFound;
        }
    }

    //old
    /*@Override
    public DocType findDocTypeByNameForBulkOcr(String docType) {
        Optional<DocType> byName = docTypeRepository.findByName(docType);
        if (byName.isPresent()) {
            return byName.get();
        }
        else {
            List<DocTypeResponseDto> list = apiGatewayManager.getAllDocType();
            saveAllDocType(list);
            Optional<DocType> optionalDocType = docTypeRepository.findByName(docType);
            if (optionalDocType.isEmpty()){
                return null;
            }
            DocType e1 = optionalDocType.get();
            docTypeFieldService.saveDocTypesFields(List.of(e1));
            return e1;
        }
    }*/

    @Override
    public DocType findDocTypeByNameForBulkOcr(String docType) {
        System.out.println("Anirban :" + docType);
        DocType docType1 = new DocType();
        String checkdoctypeexist = "";
        List<DocTypeAllResponse> list2 = apiGatewayManager.getAllPagesBYDoctype();

        for (Integer counter = 0; counter < list2.size(); counter++) {

            System.out.println("Anirban provided "+docType.toLowerCase()+" from API "+list2.get(counter).getName().toLowerCase());
            if (docType.toLowerCase().equals( list2.get(counter).getName().toLowerCase() ) ) {
                checkdoctypeexist = "found";
                docType1.setId(list2.get(counter).getId());
                docType1.setName(list2.get(counter).getName());
                docType1.setOcrstatus(list2.get(counter).getOcrstatus());
                if (list2.get(counter).getMultipagestatus().equals("yes")) {
                    docType1.setIsMultiPageDoc(true);
                } else {
                    docType1.setIsMultiPageDoc(false);
                }
                //return docType1;
            }
        }// end for

        if (checkdoctypeexist.equals("found")) {
            return   docType1;
        }
        else {
            List<DocTypeResponseDto> list = apiGatewayManager.getAllDocType();
            saveAllDocType(list);

            Optional<DocType> optionalDocType = docTypeRepository.findByName(docType);
            if (optionalDocType.isEmpty()){
                return null;
            }
            DocType e1 = optionalDocType.get();
            docTypeFieldService.saveDocTypesFields(List.of(e1));
            return e1;
        }

    }



    @Override
    public DocType createFile(MultipartFile file, String docTypeName) {
        DocType docType = findDocTypeByName(docTypeName);
        String extension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        docType.setFileName(file.getOriginalFilename());
        docType.setFileType(file.getContentType());
        docType.setKeyFileName(fileName);
        docType.setLocation(root.toString()+ "/" + fileName);
        try {
            Files.copy(file.getInputStream(), this.root.resolve(Objects.requireNonNull(fileName)), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new OcrDmsException("File already exists!", HttpStatus.BAD_REQUEST);
        }
        return docTypeRepository.save(docType);
    }

    @Override
    public DocType createFileByuser(MultipartFile file, String docTypeName,Long createBy) {
        //DocType docType = findDocTypeByName(docTypeName);
        DocType docType = new DocType();
        String extension = getFileExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
        docType.setFileName(file.getOriginalFilename());
        docType.setFileType(file.getContentType());
        docType.setKeyFileName(fileName);
        docType.setCreatedBy(createBy);
        docType.setLocation(root.toString()+ "/" + fileName);
        try {
            Files.copy(file.getInputStream(), this.root.resolve(Objects.requireNonNull(fileName)), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new OcrDmsException("File already exists!", HttpStatus.BAD_REQUEST);
        }
        return docTypeRepository.save(docType);
    }

    @Override
    public ResponseEntity<Object> getFile(String docTypeName) {
        DocType docType = findDocTypeByName(docTypeName);
        if (Objects.isNull(docType.getLocation()) || docType.getLocation().isBlank()){
            return null;
        }
        byte[] byteFile = null;
        try {
            byteFile = Files.readAllBytes(new java.io.File(docType.getLocation()).toPath());
        } catch (IOException e) {
            throw new OcrDmsException("File not found!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(docType.getFileType()))
                .body(byteFile);
    }

    @Override
    public List<DocTypeField> getDocTypeConfig(String docType) {
        DocType entity = findDocTypeByName(docType.toUpperCase());
        List<DocTypeField> allByDocTypeId = docTypeFieldService.findAllByDocTypeId(entity.getId());
        return allByDocTypeId;
    }


    @Override
    public List<AnnotatorViewDoctypeFieldByDocIDRes> getDocTypeFieldBydoctypeidUserName (Integer doctypeid){

        List<AnnotatorViewDoctypeFieldByDocIDRes> annotatorViewDoctypeFieldByDocIDResList = new ArrayList<AnnotatorViewDoctypeFieldByDocIDRes>();
        Gson gson = new Gson();
        String apiRes= apiGatewayManager.getDocTypeFieldBydoctypeid(doctypeid);
        AnnotatorViewDoctypeFieldByDocIDRes[] rootAPIResponse = gson.fromJson(apiRes, AnnotatorViewDoctypeFieldByDocIDRes[].class);

        User newuser = userService.getCurrentuser();

        System.out.println("Username "+newuser.getUsername());
        if (rootAPIResponse == null){

            return annotatorViewDoctypeFieldByDocIDResList;
        }

        if ( rootAPIResponse.length > 0){
            for (Integer counter=0; counter < rootAPIResponse.length; counter ++){

                String language = "";

                System.out.println("Anirban dpctype id="+doctypeid+" user name="+newuser.getUsername()+"doctypefield_id = "+Integer.parseInt(rootAPIResponse[counter].getId().toString()));
                language = docTypeFieldUserNameRepository.getLanguageByDoctypeandUsername( doctypeid, newuser.getUsername(),  Integer.parseInt(rootAPIResponse[counter].getId().toString()));

                /*if ( rootAPIResponse[counter].getLanguage() == null){

                    language="";
                }else{*/

                   // language= docTypeFieldUserNameRepository.getLanguageByDoctypeandUsername(doctypeid, newuser.getUsername());
                //}*/

               AnnotatorViewDoctypeFieldByDocIDRes newAnnotatorViewDoctypeFieldByDocIDRes = new AnnotatorViewDoctypeFieldByDocIDRes(
                        rootAPIResponse[counter].getId(),
                        rootAPIResponse[counter].getName(),
                        rootAPIResponse[counter].getType(),
                        rootAPIResponse[counter].getMapKey(),
                        rootAPIResponse[counter].getSequence(),
                        rootAPIResponse[counter].getCreate_at(),
                        rootAPIResponse[counter].getUpdate_at(),
                        language,
                        rootAPIResponse[counter].getDocTypeId(),
                       rootAPIResponse[counter].getDoctypepage_id()

                );

                annotatorViewDoctypeFieldByDocIDResList.add( newAnnotatorViewDoctypeFieldByDocIDRes);
            }// end loop

            return annotatorViewDoctypeFieldByDocIDResList;
        }else{
            return annotatorViewDoctypeFieldByDocIDResList;
        }// end else

    }// end function

    @Override
    public List<DocTypeUserWise> getUserWiseActiveDoctype(){

           User  user = userService.getCurrentuser();
           System.out.println("Anirban Current user id"+user.getId());
           return docTypeUserWiseRepository.getDocTypeUserWiseById(user.getId());
           //return apiGatewayManager.getApprovedDoctypeByUserId(user.getId());
    }

    @Override
    @Transactional
    @Modifying
    public AnnoViewDocTypeCreationRes createDocTypeMainClasseqByUser(DocTypeCreateMainClassDto dto){

        User user = userService.getCurrentuser();
        AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new AnnoViewDocTypeCreationRes();
        DocTypeReqByUser  docTypeReqByUser = docTypeReqByUserRepository.save( DocTypeReqByUser.builder().name(dto.getName().toLowerCase().replace(" ","_")).ocrstatus(dto.getOcrstatus())
                .pagecount(dto.getPagecount()).multipagestatus(dto.getMultipagestatus()).requserid(user.getId()).build());
        dto.setSourcereqid(docTypeReqByUser.getId());
        dto.setCreatorid(user.getId());
        dto.setCreatorname(dto.getCreatorname());
        annoViewDocTypeCreationRes = apiGatewayManager.doAnnotatorViewMainClassCreation(dto);
        docTypeReqByUserRepository.updateDoctypeIdReqByuserByid(annoViewDocTypeCreationRes.getId(), docTypeReqByUser.getId());

        return annoViewDocTypeCreationRes ;
    }

    @Override
    @Transactional
    @Modifying
    public  AnnoViewDocTypeCreationRes  createDocTypeSubClasseqByUser(DocTypeCreateSubClassDto dto){
        User user = userService.getCurrentuser();
        AnnoViewDocTypeCreationRes annoViewDocTypeCreationRes = new AnnoViewDocTypeCreationRes();
         DocTypeReqByUser  docTypeReqByUser = docTypeReqByUserRepository.save( DocTypeReqByUser.builder().name(dto.getName().toLowerCase().replace(" ","_")+dto.getPageno())
                        .parentdoctypeid(dto.getParentdoctypeid()).pageno(dto.getPageno()).subclass(dto.getSubclass()).requserid(user.getId()).build());
        dto.setSourcereqid(docTypeReqByUser.getId());
        dto.setCreatorid(user.getId());
        dto.setCreatorname(dto.getCreatorname());
        annoViewDocTypeCreationRes = apiGatewayManager.doAnnotatorViewSubClassCreation(dto);
        docTypeReqByUserRepository.updateDoctypeIdReqByuserByid(annoViewDocTypeCreationRes.getId(), docTypeReqByUser.getId());

        return annoViewDocTypeCreationRes ;
         //return apiGatewayManager.doAnnotatorViewSubClassCreation(dto);
    }

    @Override
    @Transactional
    @Modifying
    public AnnoViewDocTypeFieldCreationRes createDocTypeFieldReqByUser(DocTypeFieldCreateDto dto, Long docTypeId, Long docTypePageId) throws Exception {
        //DocTypeReqByUser docTypeReqByUser = docTypeReqByUserRepository.getDocTypeReqByUserId(docTypeId);
        DocTypeFieldReqByUser docTypeFieldReqByUser = docTypeFieldService.createDocTypeFieldReqByUser(dto,  docTypeId, docTypePageId);

        dto.setSourcereqid(docTypeFieldReqByUser.getId());
        return apiGatewayManager.doAnnoViewDocTypeFieldCreationRes( dto, docTypeId, docTypePageId);
    }

    @Override
    public DocTypeApproveRejectResponse doApproveRejectBydoctypeIduser(ApproveRejectRequest approveRejectRequest){

        //DocType docType = new DocType();
        //docType = docTypeReposit
        DocTypeApproveRejectResponse docTypeApproveRejectResponse = new DocTypeApproveRejectResponse();
        //System.out.println("Anirban Status "+approveRejectRequest.getStatus());
        if (approveRejectRequest.getStatus().equalsIgnoreCase("approved")){
            //System.out.println("Anirban Status approved "+approveRejectRequest.getStatus());
            //docTypeUserWiseRepository.save()
            // data will be insertted in doctypeuserwise table and all update will be done
            docTypeUserWiseRepository.save(DocTypeUserWise.builder()
                    .name(approveRejectRequest.getName().toLowerCase())
                    .createdby(approveRejectRequest.getUserid())
                    .doctypeid(approveRejectRequest.getDoctypeid())
                    .status(approveRejectRequest.getStatus())
                    .build());
            docTypeReqByUserRepository.updateDocTypeByIdandUserReq( approveRejectRequest.getSourcereqid(), "approved",approveRejectRequest.getUserid());
            docTypeFieldReqByUserRepository.updateDocTypeFiledByIdandUserReq(approveRejectRequest.getDoctypeid(), "approved", approveRejectRequest.getUserid());
            docTypeApproveRejectResponse.setMessage("Request approved successfully");
            docTypeApproveRejectResponse.setStatus("success");
        }else  if (approveRejectRequest.getStatus().equalsIgnoreCase("rejected")){
            // data will be only updated
            //System.out.println("Anirban Status rejected"+approveRejectRequest.getStatus());
            docTypeReqByUserRepository.updateDocTypeByIdandUserReq( approveRejectRequest.getSourcereqid(), "rejected",approveRejectRequest.getUserid());
            docTypeFieldReqByUserRepository.updateDocTypeFiledByIdandUserReq(approveRejectRequest.getDoctypeid(), "rejected", approveRejectRequest.getUserid());
            docTypeApproveRejectResponse.setMessage("Request rejected");
            docTypeApproveRejectResponse.setStatus("error");
        }
        return docTypeApproveRejectResponse;
    }

   @Override
   @Transactional
   @Modifying
   public ImportDocTypeUserwiseResponse doImportDocTypeUserwise(ImportDocTypeUserwiseRequest importDocTypeUserwiseRequest){

       ImportDocTypeUserwiseResponse importDocTypeUserwiseResponse = new ImportDocTypeUserwiseResponse();
       User user = userService.getCurrentuser();
       Boolean duplicateexist = false;
       String duplicatedocname = "";

       if (importDocTypeUserwiseRequest.getDoctypes().size() >0){
           for( Integer counter=0; counter < importDocTypeUserwiseRequest.getDoctypes().size(); counter++) {
               if ( docTypeUserWiseRepository.checkAlreadyExistDoctype(importDocTypeUserwiseRequest.getDoctypes().get(counter).toLowerCase(), user.getId()) <=0 ) {
                   docTypeUserWiseRepository.save(DocTypeUserWise.builder()
                           .name(importDocTypeUserwiseRequest.getDoctypes().get(counter).toLowerCase())
                           .createdby(user.getId())
                           .doctypeid(importDocTypeUserwiseRequest.getDoctypeids().get(counter))
                           .build());
               }else{
                   duplicateexist = true;
                   duplicatedocname = duplicatedocname + importDocTypeUserwiseRequest.getDoctypes().get(counter).toLowerCase()+",";
               }
           }
           importDocTypeUserwiseResponse.setMessage("Data uploaded");
           importDocTypeUserwiseResponse.setStatus("success");
       }

       if ( duplicateexist == true){
           throw new OcrDmsException(removeLastChar(duplicatedocname) +" document names are duplicate.");
       }
       return importDocTypeUserwiseResponse;
   }

    public String removeLastChar(String s)
    {
        //returns the string after removing the last character
        return s.substring(0, s.length() - 1);
    }
}
