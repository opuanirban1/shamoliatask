package com.dev.aes.service.impl;

import com.dev.aes.config.APIGatewayManager;
import com.dev.aes.constant.NotificationType;
import com.dev.aes.entity.*;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.DocTypeFieldCreateDto;
import com.dev.aes.payloads.request.EnterDataRequest;
import com.dev.aes.payloads.request.LanguageRequestDto;
import com.dev.aes.payloads.response.AnnotatorViewDoctypeFieldByDocIDRes;
import com.dev.aes.payloads.response.DocTypeFieldResponseDto;
import com.dev.aes.payloads.response.EnterDataResponse;
import com.dev.aes.payloads.response.Notification;
import com.dev.aes.repository.*;
import com.dev.aes.repository.Query.DocTypeFieldSpecification;
import com.dev.aes.service.DocTypeFieldService;
import com.dev.aes.service.NotificationService;
import com.dev.aes.service.UserService;
import com.google.gson.Gson;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import org.apache.tika.language.LanguageIdentifier;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocTypeFieldServiceImpl implements DocTypeFieldService {
    private final Logger LOG = LoggerFactory.getLogger(DocTypeFieldServiceImpl.class);
    private final DocTypeFieldRepository doctypeFieldRepository;
    private final APIGatewayManager apiGatewayManager;
    private final DocTypeFieldRepository docTypeFieldRepository;

    @Autowired
    FileContentFieldRepository fileContentFieldRepository;

    @Autowired
    UserService userService;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    DocTypeFieldUserNameRepository docTypeFieldUserNameRepository ;

    @Autowired
    DocTypeReqByUserRepository docTypeReqByUserRepository;

    @Autowired
     DocTypeFieldReqByUserRepository docTypeFieldReqByUserRepository;

   /* @Autowired
    NotificationType notificationType;

    @Autowired
    Notification notification;*/

    @Autowired
    NotificationService notificationService;



    @Autowired
    public DocTypeFieldServiceImpl(DocTypeFieldRepository doctypeFieldRepository,
                                   APIGatewayManager apiGatewayManager, DocTypeFieldRepository docTypeFieldRepository) {
        this.doctypeFieldRepository = doctypeFieldRepository;
        this.apiGatewayManager = apiGatewayManager;
        this.docTypeFieldRepository = docTypeFieldRepository;
    }
    @Override
    public List<DocTypeField> getDocTypeFields(Long docTypeId, Long pageNumber) {
        return docTypeFieldRepository.findAll(DocTypeFieldSpecification.getDocTypeFieldPredicate(docTypeId, pageNumber));
    }
    @Override
    public void saveDocTypesFields(List<DocType> allDocType) {
        if (allDocType == null || allDocType.isEmpty()) {
            return;
        }
        for (DocType docType : allDocType) {
            Long count = doctypeFieldRepository.countByDocTypeId(docType.getId());
            if (count == 0) {
                List<DocTypeFieldResponseDto> allDocTypeFieldByDocType = apiGatewayManager
                        .getAllDocTypeFieldByDocType(docType.getDocTypeId());
                allDocTypeFieldByDocType.forEach(responseDto -> {
                    doctypeFieldRepository.save(DocTypeField.builder()
                            .name(responseDto.getName())
                            .type(responseDto.getType())
                            .mapKey(responseDto.getMapKey())
                            .pageNumber(responseDto.getDoctypepage_id())
                            .sequence(responseDto.getSequence())
                            .docType(docType)
                            .build());
                });
            }
        }
    }

    @Override
    public List<DocTypeField> findAllByDocTypeId(Long id) {
        return doctypeFieldRepository.findAllByDocTypeId(id);
    }


    //old
    @Override
    public DocTypeField updateLanguage(LanguageRequestDto dto, Long id) {
        DocTypeField entity = doctypeFieldRepository.findById(id).orElseThrow(() -> new OcrDmsException("DocTypeField Not Found"));
        entity.setLanguage(dto.getLanguage());
        return doctypeFieldRepository.save(entity);
    }


    @Override
    public DocTypeFieldusername updateLanguageUsername(LanguageRequestDto dto, Long id) {
        //DocTypeField entity = doctypeFieldRepository.findById(id).orElseThrow(() -> new OcrDmsException("DocTypeField Not Found"));
        DocTypeFieldusername entity = new DocTypeFieldusername();
        String apiResponse = apiGatewayManager.getDocTypeFieldByid(Integer.parseInt(id.toString()));

        Gson gson = new Gson();
        AnnotatorViewDoctypeFieldByDocIDRes  annotatorViewDoctypeFieldByDocIDRes = gson.fromJson(apiResponse, AnnotatorViewDoctypeFieldByDocIDRes.class);

        annotatorViewDoctypeFieldByDocIDRes.setLanguage(dto.getLanguage());
        User user = userService.getCurrentuser();

      entity.setType(annotatorViewDoctypeFieldByDocIDRes.getType());
        entity.setSequence(annotatorViewDoctypeFieldByDocIDRes.getSequence());
        entity.setMapKey(annotatorViewDoctypeFieldByDocIDRes.getMapKey());
        entity.setName(annotatorViewDoctypeFieldByDocIDRes.getName());
        entity.setPageNumber(Long.parseLong(annotatorViewDoctypeFieldByDocIDRes.getDoctypepage_id().toString()));
        entity.setDoctype_id(annotatorViewDoctypeFieldByDocIDRes.getDocTypeId());
        entity.setLanguage(dto.getLanguage());
        entity.setUsername(user.getUsername());
        entity.setDoctypefield_id(Integer.parseInt(id.toString()));
        return docTypeFieldUserNameRepository.save(entity);
    }


    @Transactional
    @Modifying
    @Override
    public EnterDataResponse insertUpdateDoctypeField (EnterDataRequest enterDataRequest){
        EnterDataResponse enterDataResponse = new EnterDataResponse();
        //LOG.info("size {} ", enterDataRequest.getFilecontentfieldid().size());
       // if (enterDataRequest.getFilecontentfieldid().size()> 0){
            //update
            //for (Integer i=0; i < enterDataRequest.getName().size(); i++ ){
              /*  String language = "";
                if (enterDataRequest.getLanguage().size() <= 0){
                    language = "";
                }else{
                    language = enterDataRequest.getLanguage().get(enterDataRequest.getLanguage().keySet().toArray()[i]);
                }*/
               /* fileContentFieldRepository.save(FileContentField.builder()
                        .name(enterDataRequest.getName().get(enterDataRequest.getName().keySet().toArray()[i]))
                        .ocrValue(enterDataRequest.getInputdata().get(enterDataRequest.getInputdata().keySet().toArray()[i]))
                        .type(enterDataRequest.getType().get(enterDataRequest.getType().keySet().toArray()[i]))
                        .doctypeFieldId(enterDataRequest.getDoctypeFieldId().get(enterDataRequest.getDoctypeFieldId().keySet().toArray()[i]))
                        .sequence(Integer.valueOf(enterDataRequest.getName().keySet().toArray()[i].toString()))
                        .mapkey(enterDataRequest.getMapkey().get(enterDataRequest.getMapkey().keySet().toArray()[i]))
                        .language(enterDataRequest.getLanguage().get(enterDataRequest.getLanguage().keySet().toArray()[i]))
                        .doctypeFieldId(enterDataRequest.getDoctypeFieldId().get(enterDataRequest.getDoctypeFieldId().keySet().toArray()[i]))
                        .id(enterDataRequest.getFilecontentfieldid().get(enterDataRequest.getFilecontentfieldid().keySet().toArray()[i]))
                        .createdBy(userService.getCurrentuser())
                        .doctypeId(enterDataRequest.getDoctypeId())
                        .doctypemainclassid(enterDataRequest.getDoctypemainclassId())
                        .fileId(enterDataRequest.getFileId())
                        .build());

            }// end loop

        }else{*/
            //insert
              //System.out.println("***** Anirban file id"+enterDataRequest.getFileId()+" doc type id "+ enterDataRequest.getDoctypeId());

              //fileContentFieldRepository.deleteInfoFromfileContentField(enterDataRequest.getFileId(), enterDataRequest.getDoctypeId());
             fileContentFieldRepository.deleteInfoFromfileContentFieldByFileID(enterDataRequest.getFileId());
            for (Integer k=0; k < enterDataRequest.getName().size(); k++ ){
            /* String language = "";
             if (enterDataRequest.getLanguage().size() <= 0){
                 language = "";\
             }else{
                 language = enterDataRequest.getLanguage().get(enterDataRequest.getLanguage().keySet().toArray()[k]);
             }*/
                fileContentFieldRepository.save(FileContentField.builder()
                        .name(enterDataRequest.getName().get(enterDataRequest.getName().keySet().toArray()[k]))
                        .ocrValue(enterDataRequest.getInputdata().get(enterDataRequest.getInputdata().keySet().toArray()[k]))
                        .type(enterDataRequest.getType().get(enterDataRequest.getType().keySet().toArray()[k]))
                        .doctypeFieldId(enterDataRequest.getDoctypeFieldId().get(enterDataRequest.getDoctypeFieldId().keySet().toArray()[k]))
                        .sequence(Integer.valueOf(enterDataRequest.getName().keySet().toArray()[k].toString()))
                        .mapkey(enterDataRequest.getMapkey().get(enterDataRequest.getMapkey().keySet().toArray()[k]))
                        .language(enterDataRequest.getLanguage().get(enterDataRequest.getLanguage().keySet().toArray()[k]))
                        //.doctypeFieldId(enterDataRequest.getDoctypeFieldId().get(enterDataRequest.getDoctypeFieldId().keySet().toArray()[k]))
                        .createdBy(userService.getCurrentuser())
                        .doctypeId(enterDataRequest.getDoctypeId())
                        .doctypemainclassid(enterDataRequest.getDoctypemainclassId())
                        .fileId(enterDataRequest.getFileId())
                        .build());
            }// end loop
        //}// end else

        //System.out.println("****Anirban "+enterDataRequest.getDoctypeName());
        fileRepository.updateFilestatusByidandStatustext("CONFIRMED", Integer.parseInt(enterDataRequest.getFileId().toString()), enterDataRequest.getDoctypeName());


        notificationService.sendMessagesNotification(Notification.builder()
                        .folderId(fileRepository.getFolderIdByFileId(enterDataRequest.getFileId()))
                        .fileId(enterDataRequest.getFileId())
                .type(NotificationType.DATA_ENTERED.toString())
                .status(Boolean.TRUE)
                .build());


        enterDataResponse.setMessage("Action done");
        enterDataResponse.setStatus("success");
        LOG.info("API response {}", enterDataResponse);
        return enterDataResponse;
    }

    @Override
    public List<FileContentField> getFileContentDataByFileId (Long id){
        List<FileContentField> fileContentFields = new ArrayList<>();
        fileContentFields = fileContentFieldRepository.getFileContentDataByFileIdDB(id);
        return fileContentFields;

    }


    @Modifying
    @Override
    @Transactional
    public DocTypeFieldReqByUser createDocTypeFieldReqByUser(DocTypeFieldCreateDto dto, Long doctypeid, Long docTypePageId) throws Exception {

        User user = userService.getCurrentuser();
        return  docTypeFieldReqByUserRepository.save(DocTypeFieldReqByUser.builder().name(dto.getName()).type(dto.getType()).docTypeId(doctypeid).doctypepage_id(docTypePageId)
                        .mapKey(getMapkeyByinfo(dto.getName())).sequence(dto.getSequence()).requserid(user.getId()).build());
    }


    public String getMapkeyByinfo(String input) throws Exception {
        if (input == null){
            return "";
        }else {
            if (input.equals("")){
                return "";
            }else{
                LanguageIdentifier identifier = new LanguageIdentifier(input);
                String language = identifier.getLanguage();
                System.out.println("Language code is : " + language);
                if (language.equals("unknown")){
                    String translatedinfo =  "b_"+apiGatewayManager.getTranslationInfo( input, "en");
                    return translatedinfo.replaceAll("[^a-zA-Z0-9 \\s]", "").replace(" ", "_").toLowerCase();
                }else{
                    return input.replaceAll("[^a-zA-Z0-9 \\s]", "").replace(" ", "_").toLowerCase();
                }
                /*LanguageDetector detector = LanguageDetector.getDefaultLanguageDetector().loadModels();
                detector.addText("This is english");
                LanguageResult languageResult = detector.detect();*/
                //.replaceAll("[^a-zA-Z0-9\\s]", "");
                //res = input.replaceAll("[^a-zA-Z0-9 \\s]", "");
                //res = res.toLowerCase();
                //res = res.replace(" ", "_");
                // return input.replaceAll("[^a-zA-Z0-9 \\s]", "").replace(" ", "_").toLowerCase();
            }
        }
    }// end function
}
