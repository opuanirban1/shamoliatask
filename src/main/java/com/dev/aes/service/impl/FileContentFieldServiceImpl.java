package com.dev.aes.service.impl;

import com.dev.aes.config.APIGatewayManager;
import com.dev.aes.entity.*;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.FileContentFieldUpdateDto;
import com.dev.aes.payloads.response.AnnotatorViewDoctypeFieldByDocIDRes;
import com.dev.aes.payloads.response.SearchResponseDto;
import com.dev.aes.repository.FileContentFieldRepository;
import com.dev.aes.service.DocTypeFieldService;
import com.dev.aes.service.FileContentFieldService;
import com.dev.aes.service.UserService;
import com.dev.aes.util.SystemDateUtil;
import com.google.gson.Gson;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class FileContentFieldServiceImpl implements FileContentFieldService {
    private final FileContentFieldRepository fileContentFieldRepository;
    private final DocTypeFieldService docTypeFieldService;
    private final UserService userService;

    @Autowired
    APIGatewayManager apiGatewayManager;

    @Autowired
    public FileContentFieldServiceImpl(FileContentFieldRepository fileContentFieldRepository,
                                       DocTypeFieldService docTypeFieldService,
                                       UserService userService) {
        this.fileContentFieldRepository = fileContentFieldRepository;
        this.docTypeFieldService = docTypeFieldService;
        this.userService = userService;
    }

    @Override
    public List<FileContentField> getFieldContentFieldByFile(DocFile docFile) throws ParseException {
        List<FileContentField> byFileIdOrderBySequence = fileContentFieldRepository.findByFileIdOrderBySequence(docFile.getId());
        for (FileContentField fileContentField : byFileIdOrderBySequence) {
            if (Objects.isNull(fileContentField.getOcrValue()) || fileContentField.getOcrValue().equals("null")) {
                fileContentField.setOcrValue(null);
                continue;
            }
            if (fileContentField.getType().equalsIgnoreCase("Date")) {
                //old
                // fileContentField.setOcrValue(SystemDateUtil.parseBanglaOrEnglishDate(fileContentField.getOcrValue()));
                //getNewDateFormatFromUser (String input)
                fileContentField.setOcrValue(SystemDateUtil.getNewDateFormatFromUser(fileContentField.getOcrValue()));
             }
        }
        return byFileIdOrderBySequence;
    }


    @Override
    @Transactional
    public void update(FileContentFieldUpdateDto dto) {
        FileContentField fileContentField = fileContentFieldRepository.findById(dto.getId())
                .orElseThrow(() -> new OcrDmsException("Field not found!", HttpStatus.BAD_REQUEST));
        fileContentField.setOcrValue(dto.getOcrValue());
        fileContentFieldRepository.save(fileContentField);
    }

    //old
    /*@Override
    @Transactional
    public void save(Map<String, String> map, DocFile docFile, DocType docType) {
        List<DocTypeField> docTypeFields = docTypeFieldService.findAllByDocTypeId(docType.getId());
        for (DocTypeField docTypeField : docTypeFields) {
            String value = map.getOrDefault(docTypeField.getMapKey(), "");
            User user = userService.findUserById(docFile.getCreatedById());
            FileContentField save = fileContentFieldRepository.save(
                    FileContentField.builder()
                            .name(docTypeField.getName())
                            .ocrValue(value)
                            .doctypeId(docType.getId())
                            .doctypeFieldId(docTypeField.getId())
                            .type(docTypeField.getType())
                            .sequence(docTypeField.getSequence())
                            .language(docTypeField.getLanguage())
                            .fileId(docFile.getId())
                            .createdBy(user)
                            .build()
            );
        }
    }*/

    @Transactional
    public void save(Map<String, String> map, DocFile docFile, DocType docType) {
        /*List<DocTypeField> docTypeFields = docTypeFieldService.findAllByDocTypeId(docType.getId());
        for (DocTypeField docTypeField : docTypeFields) {
            String value = map.getOrDefault(docTypeField.getMapKey(), "");
            User user = userService.findUserById(docFile.getCreatedById());
            FileContentField save = fileContentFieldRepository.save(
                    FileContentField.builder()
                            .name(docTypeField.getName())
                            .ocrValue(value)
                            .doctypeId(docType.getId())
                            .doctypeFieldId(docTypeField.getId())
                            .type(docTypeField.getType())
                            .sequence(docTypeField.getSequence())
                            .language(docTypeField.getLanguage())
                            .fileId(docFile.getId())
                            .createdBy(user)
                            .build()
            );
        }*/
        String  doctypeid = apiGatewayManager.getDoctypeIdByName(docType.getName());
        String apiResponse = apiGatewayManager.getDocTypeFieldBydoctypeid(Integer.parseInt(doctypeid));
        //System.out.println("******Anirban API response "+ apiResponse);
        Gson gson = new Gson();
        AnnotatorViewDoctypeFieldByDocIDRes[] rootAPIresponse = gson.fromJson(apiResponse, AnnotatorViewDoctypeFieldByDocIDRes[].class);
        // for (DocTypeField docTypeField : docTypeFields) {
        //adding new for removing dupilcate entry
        fileContentFieldRepository.deleteInfoFromfileContentFieldByFileID(docFile.getId());
        for (Integer counter=0; counter < rootAPIresponse.length; counter ++ ){
            String value = map.getOrDefault(rootAPIresponse[counter].getMapKey(), "");
            //User user = userService.findUserById(docFile.getCreatedById());
            //User user = userService.getCurrentuser();
            User user = userService.findUserById(docFile.getCreatedById());
            /*System.out.println("*******Anirban data insert "+counter+" name"+rootAPIresponse[counter].getName()+" value"+
                    value+" doctype id"+ Long.parseLong(doctypeid.toString())+"doctype field id"+rootAPIresponse[counter].getId()+ rootAPIresponse[counter].getType()
            +"Sequence "+ rootAPIresponse[counter].getSequence()+ " language "+ rootAPIresponse[counter].getLanguage()+
                    " file id"+ fileid+ "create by "+user);*/
            FileContentField save = fileContentFieldRepository.save(
                    FileContentField.builder()
                            .name(rootAPIresponse[counter].getName())
                            .ocrValue(value)
                            .doctypeId(Long.parseLong(docType.getId().toString()))
                            .doctypeFieldId(rootAPIresponse[counter].getId())
                            .type(rootAPIresponse[counter].getType())
                            .sequence(rootAPIresponse[counter].getSequence())
                            .language(rootAPIresponse[counter].getLanguage())
                            .fileId(Long.parseLong(docFile.getId().toString()))
                            .createdBy(user)
                            .build()
            );
        }
    }

    @Override
    public List<SearchResponseDto> search(String value, List<Long> userIds) {
        List<FileContentField> fileContentFields = fileContentFieldRepository.search(value, userIds);
        if (!fileContentFields.isEmpty()){
            return fileContentFields.stream().map(fileContentField -> SearchResponseDto.builder()
                            .id(fileContentField.getId())
                            .content(fileContentField.getOcrValue())
                            .type("FILE_CONTENT").build())
                    .collect(Collectors.toList());
        }
        return List.of();
    }




    @Override
    @Transactional
    public void saveReRunOcr(Map<String, String> map, Integer  doctypeid, Integer fileid) {
        //List<DocTypeField> docTypeFields = docTypeFieldService.findAllByDocTypeId(docType.getId());
        String apiResponse = apiGatewayManager.getDocTypeFieldBydoctypeid(doctypeid);
        //System.out.println("******Anirban API response "+ apiResponse);
        Gson gson = new Gson();
        AnnotatorViewDoctypeFieldByDocIDRes[] rootAPIresponse = gson.fromJson(apiResponse, AnnotatorViewDoctypeFieldByDocIDRes[].class);
       // for (DocTypeField docTypeField : docTypeFields) {
         for (Integer counter=0; counter < rootAPIresponse.length; counter ++ ){
            String value = map.getOrDefault(rootAPIresponse[counter].getMapKey(), "");
            //User user = userService.findUserById(docFile.getCreatedById());
             User user = userService.getCurrentuser();
            /*System.out.println("*******Anirban data insert "+counter+" name"+rootAPIresponse[counter].getName()+" value"+
                    value+" doctype id"+ Long.parseLong(doctypeid.toString())+"doctype field id"+rootAPIresponse[counter].getId()+ rootAPIresponse[counter].getType()
            +"Sequence "+ rootAPIresponse[counter].getSequence()+ " language "+ rootAPIresponse[counter].getLanguage()+
                    " file id"+ fileid+ "create by "+user);*/
            FileContentField save = fileContentFieldRepository.save(
                    FileContentField.builder()
                            .name(rootAPIresponse[counter].getName())
                            .ocrValue(value)
                            .doctypeId(Long.parseLong(doctypeid.toString()))
                            .doctypeFieldId(rootAPIresponse[counter].getId())
                            .type(rootAPIresponse[counter].getType())
                            .sequence(rootAPIresponse[counter].getSequence())
                            .language(rootAPIresponse[counter].getLanguage())
                            .fileId(Long.parseLong(fileid.toString()))
                            .createdBy(user)
                            .build()
            );
        }
    }// end function


    @Override
    @Transactional
    public void saveRunOcr(Map<String, String> map, Integer  doctypeid, Integer fileid) {
        //List<DocTypeField> docTypeFields = docTypeFieldService.findAllByDocTypeId(docType.getId());
        String apiResponse = apiGatewayManager.getDocTypeFieldBydoctypeid(doctypeid);
        //System.out.println("******Anirban API response "+ apiResponse);
        Gson gson = new Gson();
        AnnotatorViewDoctypeFieldByDocIDRes[] rootAPIresponse = gson.fromJson(apiResponse, AnnotatorViewDoctypeFieldByDocIDRes[].class);
        // for (DocTypeField docTypeField : docTypeFields) {
        for (Integer counter=0; counter < rootAPIresponse.length; counter ++ ){
            String value = map.getOrDefault(rootAPIresponse[counter].getMapKey(), "");
            //User user = userService.findUserById(docFile.getCreatedById());
            System.out.println("Anirban Makpey :"+rootAPIresponse[counter].getMapKey()+"Anirban Value :"+value );
            User user = userService.getCurrentuser();
            /*System.out.println("*******Anirban data insert "+counter+" name"+rootAPIresponse[counter].getName()+" value"+
                    value+" doctype id"+ Long.parseLong(doctypeid.toString())+"doctype field id"+rootAPIresponse[counter].getId()+ rootAPIresponse[counter].getType()
            +"Sequence "+ rootAPIresponse[counter].getSequence()+ " language "+ rootAPIresponse[counter].getLanguage()+
                    " file id"+ fileid+ "create by "+user);*/
           /* FileContentField save = fileContentFieldRepository.save(
                    FileContentField.builder()
                            .name(rootAPIresponse[counter].getName())
                            .ocrValue(value.toString())
                            .doctypeId(Long.parseLong(doctypeid.toString()))
                            .doctypeFieldId(rootAPIresponse[counter].getId())
                            .type(rootAPIresponse[counter].getType())
                            .sequence(rootAPIresponse[counter].getSequence())
                            .language(rootAPIresponse[counter].getLanguage())
                            .fileId(Long.parseLong(fileid.toString()))
                            .createdBy(user)
                            .build()
            );*/

            fileContentFieldRepository.insertFileContentData (
                    rootAPIresponse[counter].getName(),
                    value.toString(),
                    Long.parseLong(doctypeid.toString()),
                    rootAPIresponse[counter].getId(),
                    rootAPIresponse[counter].getType(),
                    rootAPIresponse[counter].getSequence(),
                    rootAPIresponse[counter].getLanguage(),
                    Long.parseLong(fileid.toString()),
                    user.getId(),
                    rootAPIresponse[counter].getMapKey()
            );
        }// end for
    }// end function
}
