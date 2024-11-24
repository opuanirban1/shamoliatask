package com.dev.aes.service.impl;

import com.dev.aes.config.APIGatewayManager;
import com.dev.aes.config.FileStorageProperties;
import com.dev.aes.constant.NotificationType;
import com.dev.aes.controller.FileController;
import com.dev.aes.entity.*;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.*;
import com.dev.aes.payloads.response.*;
import com.dev.aes.repository.BulkFileDoOcrRepository;
import com.dev.aes.repository.DocTypeUserWiseRepository;
import com.dev.aes.repository.FileContentFieldRepository;
import com.dev.aes.repository.FileRepository;
import com.dev.aes.service.*;
import com.dev.aes.util.LocalServerProperties;
import com.dev.aes.util.SystemDateUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    private final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    FileContentFieldRepository fileContentFieldRepository;

    @Autowired
    SystemDateUtil systemDateUtil;

    @Autowired
    BulkFileDoOcrRepository bulkFileDoOcrRepository ;

    @Autowired
    DocTypeUserWiseRepository docTypeUserWiseRepository;

    private final FileRepository fileRepository;
    private final FolderService folderService;
    private final APIGatewayManager apiGatewayManager;
    private final FileContentFieldService fileContentFieldService;
    private final DocTypeService docTypeService;
    private final NotificationService notificationService;
    private final UserService userService;
    private final LocalServerProperties localServerProperties;
    private final String rootPath;


    private final Path root;
    /*private final Set<String> fileExtentionSet = Set.of(".pdf", ".PDF", ".doc",
            ".docx", ".xlsx", ".csv", ".xls", ".jpeg", ".JPEG", ".jpg", ".JPG", ".png", ".PNG");*/
    private final Set<String> fileExtentionSet = Set.of(".pdf", ".PDF", ".jpeg", ".JPEG", ".jpg", ".JPG", ".png", ".PNG");

    @Autowired
    public FileServiceImpl(FileRepository fileRepository,
                           FolderService folderService,
                           APIGatewayManager apiGatewayManager,
                           FileContentFieldService fileContentFieldService,
                           DocTypeService docTypeService,
                           NotificationService notificationService, UserService userService,
                           LocalServerProperties localServerProperties,
                           FileStorageProperties fileStorageProperties) {
        this.fileRepository = fileRepository;
        this.folderService = folderService;
        this.apiGatewayManager = apiGatewayManager;
        this.fileContentFieldService = fileContentFieldService;
        this.docTypeService = docTypeService;
        this.notificationService = notificationService;
        this.userService = userService;
        this.localServerProperties = localServerProperties;
        this.rootPath = fileStorageProperties.getUploadDir();
        this.root = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.root);
        } catch (Exception ex) {
            throw new OcrDmsException("Could not create the directory where the uploaded files will be stored.");
        }
    }

    @Override
    @Transactional
    public DocFile createFile(MultipartFile file, Long folderId){

        //try {
            if (file.isEmpty()) {
                throw new OcrDmsException("There is no file in request.", HttpStatus.BAD_REQUEST);
            }
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            if (!fileExtentionSet.contains(extension)) {
                throw new OcrDmsException("This file can not upload", HttpStatus.BAD_REQUEST);
            }
            if (fileRepository.existsByFileNameAndFolderId(originalFilename, folderId)) {
                Long count = fileRepository
                        .countByFileNameContainingAndFolderId
                                (originalFilename.substring(0, originalFilename.lastIndexOf(".") - 1), folderId);
                originalFilename = originalFilename
                        .substring(0, originalFilename.lastIndexOf(".")) + "(" + count + ")" + extension;
            }
            DocFile newDocFile = new DocFile();
            Folder folder = folderService.findById(folderId);
            String path = rootPath + File.separator + getFolderPathByTraversing(folder);
            String fileName = UUID.randomUUID().toString().replace("-", "") + extension;
            newDocFile.setFileName(originalFilename);
            newDocFile.setFileType(file.getContentType());
            newDocFile.setKeyFileName(fileName);
            newDocFile.setLocation(path + File.separator + fileName);
            newDocFile.setFolder(folder);
            newDocFile.setStatus("NEW");

            Path fileSaveDir = Paths.get(path).toAbsolutePath().normalize();
            try {
                Files.copy(file.getInputStream(), fileSaveDir.resolve(Objects.requireNonNull(fileName)), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new OcrDmsException("File already exists!", HttpStatus.BAD_REQUEST);
            }
            newDocFile.setCreatedBy(userService.getCurrentuser());
            DocFile save = fileRepository.save(newDocFile);
            save.setDocType("NOT_DETECTED");
            save = fileRepository.save(save);
            sendNotification(Notification.builder()
                    .type(NotificationType.UPLOAD.toString())
                    .fileId(save.getId())
                    .folderId(save.getFolder().getId())
                    .status(Boolean.TRUE)
                    .build());
            return save;
        /*}catch (Exception ex){

            ex.printStackTrace();
            return null;
        }*/
    }

    private String getFolderPathByTraversing(Folder currentFolder) {
        if (currentFolder.getParent() == null) {
            return currentFolder.getName();
        }
        Folder parentFolder = folderService.findById(currentFolder.getParent().getId());
        return getFolderPathByTraversing(parentFolder) + File.separator + currentFolder.getName();
    }

   /* @Override
    public ResponseEntity<Object> getFile(Long id) {
        DocFile docFile = getFileById(id);
        byte[] byteFile = null;
        try {
            byteFile = Files.readAllBytes(new java.io.File(docFile.getLocation()).toPath());

        } catch (IOException e) {
            throw new OcrDmsException("File not found!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(docFile.getFileType()))
                .body(byteFile);
    }*/

    @Override
    public ResponseEntity<Object> getFile(Long id) {
        DocFile docFile = getFileById(id);
        String filePath = getFolderPathByTraversing(docFile.getFolder()) + File.separator + docFile.getKeyFileName();
        Path path = Paths.get(rootPath.concat(File.separator).concat(filePath));
        byte[] byteFile = null;
        try {
            byteFile = Files.readAllBytes(path);
        } catch (IOException e) {
            throw new OcrDmsException("File not found!", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(docFile.getFileType()))
                .body(byteFile);
    }

    @Override
    public DocFile getFileById(Long id) {
        System.out.println("Anirban File id "+id);
        return fileRepository.findById(id)
                .orElseThrow(() -> new OcrDmsException("File not found!", HttpStatus.BAD_REQUEST));
    }

   /* @Override
    public DocFile getFileById(Long id) {
        try {
            LOG.info("ANIRBAN FILE opu{}", id);
            DocFile docFile = new DocFile();

            docFile = fileRepository.findDocFileById(id);

            LOG.info("ANIRBAN SHOW {}",docFile.getFileName());
            return docFile;
        }catch (Exception ex) {
                //.orElseThrow(() -> new OcrDmsException("File not found!", HttpStatus.BAD_REQUEST));
            ex.printStackTrace();
            new OcrDmsException("File not found! opu", HttpStatus.BAD_REQUEST);
        }

        return null;
    }*/

    @Override
    public List<FileContentField> runOcr(Long id) throws ParseException {
        DocFile docFile = getFileById(id);
        if (docFile.getStatus().equalsIgnoreCase("OCR FINISHED") ||
                docFile.getStatus().equalsIgnoreCase("CONFIRMED")) {
            throw new OcrDmsException("File already processed!", HttpStatus.BAD_REQUEST);
        }
        docFile.setStatus("RUNNING");
        fileRepository.save(docFile);
        LocalTime start = LocalTime.now();
        DocType docType = docTypeService.findDocTypeByNameForBulkOcr(docFile.getDocType());
        if (Objects.isNull(docType)) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            throw new OcrDmsException("DocType not found", HttpStatus.NOT_FOUND);
        }
        Map<String, String> map = null;
        try {
            map = apiGatewayManager.getRunOcrResponse(docFile.getLocation(), docFile.getDocType());
        } catch (Exception e) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            log.error("Error: ", e.getMessage());
            throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(map) || map.keySet().isEmpty()) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
        }
        fileContentFieldService.save(map, docFile, docType);
        docFile.setStatus("OCR FINISHED");
        docFile.setDoOcrDurationInSec(Duration.between(start, LocalTime.now()).getSeconds());
        sendNotification(Notification.builder()
                .type(NotificationType.OCR.toString())
                .fileId(docFile.getId())
                .folderId(docFile.getFolder().getId())
                .status(Boolean.TRUE)
                .build());
        fileRepository.save(docFile);
        return fileContentFieldService.getFieldContentFieldByFile(docFile);

    }


    public List<FileContentField> runOcrWithoutMultipleAttemptCheck(Long id, String doctype, Integer doctypeid) throws ParseException {
      //try {
          DocFile docFile = getFileById(id);
        /*if (docFile.getStatus().equalsIgnoreCase("OCR FINISHED") ||
                docFile.getStatus().equalsIgnoreCase("CONFIRMED")) {
            throw new OcrDmsException("File already processed!", HttpStatus.BAD_REQUEST);
        }*/
          docFile.setStatus("RUNNING");
          fileRepository.save(docFile);
          LocalTime start = LocalTime.now();
          DocType docType = docTypeService.findDocTypeByNameForBulkOcr(docFile.getDocType());
        /*if (Objects.isNull(docType)) {
            docFile.setStatus("FAILED");
            docFile.setDocType(doctype);
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            throw new OcrDmsException("DocType not found", HttpStatus.NOT_FOUND);
        }*/
          Map<String, String> map = null;
          try {
              map = apiGatewayManager.getRunOcrResponse(docFile.getLocation(), docFile.getDocType());
          } catch (Exception e) {
              docFile.setStatus("FAILED");
              docFile.setDocType(doctype);
              sendNotification(Notification.builder()
                      .type(NotificationType.OCR.toString())
                      .fileId(docFile.getId())
                      .folderId(docFile.getFolder().getId())
                      .status(Boolean.FALSE)
                      .build());
              fileRepository.save(docFile);
              log.error("Error: ", e.getMessage());
              throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
          }
          if (Objects.isNull(map) || map.keySet().isEmpty()) {
              docFile.setStatus("FAILED");
              docFile.setDocType(doctype);
              sendNotification(Notification.builder()
                      .type(NotificationType.OCR.toString())
                      .fileId(docFile.getId())
                      .folderId(docFile.getFolder().getId())
                      .status(Boolean.FALSE)
                      .build());
              fileRepository.save(docFile);
              throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
          }

          LOG.info("Anirban**** inserted data in file content field {} data count {} table {}  {} ", map.values(), map.size(), docFile, docType);

          //Call Annotatorview API for doctypefield data by doctype id
          //Gson gson = new Gson();
          //String apiRes= apiGatewayManager.getDocTypeFieldBydoctypeid(doctypeid);

          //AnnotatorViewDoctypeFieldByDocIDRes[] rootAPIResponse = gson.fromJson(apiRes, AnnotatorViewDoctypeFieldByDocIDRes[].class);

          //LOG.info("***ANIRBAN*** API RES {} mapkey size {} and apiRes size {}", apiRes, map.size(),  rootAPIResponse.length);
          //FileContentField fileContentField = new FileContentField();
          //String language = "";
          fileContentFieldRepository.deleteInfoFromfileContentFieldByFileID(id);
          //if (rootAPIResponse.length != map.size()){
            //  LOG.info("NOT matched ***ANIRBAN*** API RES {} mapkey size {} and apiRes size {}", apiRes, map.size(),  rootAPIResponse.length);
          //}
          //else if (rootAPIResponse.length > 0  && rootAPIResponse.length == map.size()) {
              //for (Integer counter = 0; counter < rootAPIResponse.length; counter++) {
                /*  if (rootAPIResponse[counter].getLanguage().toString() == null) {
                      language = "";
                  } else {
                      language = rootAPIResponse[counter].getLanguage();
                  }*/
                  /*fileContentFieldRepository.save(
                          FileContentField.builder()
                                  .name(rootAPIResponse[counter].getName())
                                  .ocrValue(map.get(map.keySet().toArray()[counter]))
                                  .type(rootAPIResponse[counter].getType().toString())
                                  .doctypeFieldId(rootAPIResponse[counter].getId())
                                  .sequence(rootAPIResponse[counter].getSequence())
                                  .mapkey(rootAPIResponse[counter].getMapKey().toString())
                                  //.language(language)
                                  .createdBy(userService.getCurrentuser())
                                  .doctypeId(Long.parseLong(doctypeid.toString()))
                                  .doctypemainclassid(0)
                                  .fileId(id)
                                  .build()
                  );


              }// end for

          }// end if*/

          //fileContentFieldService.save(map, docFile, docType);
          fileContentFieldService.saveReRunOcr(map, doctypeid, Integer.parseInt(id.toString()));
          docFile.setStatus("OCR FINISHED");
          docFile.setDocType(doctype);
          docFile.setDoOcrDurationInSec(Duration.between(start, LocalTime.now()).getSeconds());
          sendNotification(Notification.builder()
                  .type(NotificationType.OCR.toString())
                  .fileId(docFile.getId())
                  .folderId(docFile.getFolder().getId())
                  .status(Boolean.TRUE)
                  .build());

          LOG.info("Anirban inserted data in files table {} ", docFile);

          fileRepository.save(docFile);
          LOG.info("ANIRBN respone {}", fileContentFieldService.getFieldContentFieldByFile(docFile));
          return fileContentFieldService.getFieldContentFieldByFile(docFile);
     /* }catch (Exception ex){

          ex.printStackTrace();
          return null;
      }*/
    }

    @Override
    public FileInfoDto getFileInfo(Long id) throws ParseException {
        DocFile docFile = getFileById(id);
        String fileLoc = getFileLocation(docFile);
        return FileInfoDto.builder()
                .id(docFile.getId())
                .fileName(docFile.getFileName())
                .fileType(docFile.getFileType())
                .docType(docFile.getDocType())
                .location(docFile.getLocation())
                .filePath(fileLoc)
                .status(docFile.getStatus())
                .createdAt(docFile.getCreatedAt())
                .updatedAt(docFile.getUpdatedAt())
                .fileContentFieldList(fileContentFieldService.getFieldContentFieldByFile(docFile))
                .build();
    }

    private String getFileLocation(DocFile docFile) {
        String fileLocation = "";
        Folder folder = docFile.getFolder();
        fileLocation = docFile.getFolder().getName();
        while(folder.getParent() != null) {
            folder = folderService.findById(folder.getId()).getParent();
            fileLocation = folder.getName().concat(" / ").concat(fileLocation);
        }

        return fileLocation;
    }

    @Override
    public void doBulkOcr(DocFile docFile) {
        if (docFile.getStatus().equalsIgnoreCase("OCR FINISHED") ||
                docFile.getStatus().equalsIgnoreCase("CONFIRMED")) {
            return;
        }
        docFile.setStatus("RUNNING");
        fileRepository.save(docFile);
        LocalTime start = LocalTime.now();
        //DocType docType = docTypeService.findDocTypeByNameForBulkOcr(docFile.getDocType());
        /*********************/
        DocType docType = new DocType();
        List<DocTypeAllResponse> apiInfo = apiGatewayManager.getAllPagesBYDoctype();
        for (Integer counter = 0; counter < apiInfo.size(); counter++) {

            System.out.println("Anirban provided "+docFile.getDocType().toLowerCase()+" from API "+apiInfo.get(counter).getName().toLowerCase());
            if (docFile.getDocType().toLowerCase().equals( apiInfo.get(counter).getName().toLowerCase() ) ) {
                docType.setId(apiInfo.get(counter).getId());
                docType.setName(apiInfo.get(counter).getName());
                docType.setOcrstatus(apiInfo.get(counter).getOcrstatus());
                if (apiInfo.get(counter).getMultipagestatus().equals("yes")) {
                    docType.setIsMultiPageDoc(true);
                } else {
                    docType.setIsMultiPageDoc(false);
                }
                //return docType1;
            }
        }// end for
        /******************/

        //if (Objects.isNull(docType)) {
        if (/*Objects.isNull(apiInfo)*/docType.getName() == null){
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            return;
        }

        Map<String, String> map = null;
        try {
            map = apiGatewayManager.getRunOcrResponse(docFile.getLocation(), docFile.getDocType());
        } catch (Exception e) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            log.error("Error: ", e.getMessage());
            throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(map) || map.keySet().isEmpty()) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
        }
       /* if (Objects.isNull(map) || map.keySet().isEmpty()) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            return;
        }*/
        log.info("File Content Response File Id({}): {}", docFile.getId(), map);

        fileContentFieldService.save(map, docFile, docType);
        docFile.setStatus("OCR FINISHED");
        docFile.setDoOcrDurationInSec(Duration.between(start, LocalTime.now()).getSeconds());
        sendNotification(Notification.builder()
                .type(NotificationType.OCR.toString())
                .fileId(docFile.getId())
                .folderId(docFile.getFolder().getId())
                .status(Boolean.TRUE)
                .build());
        fileRepository.save(docFile);
    }

    @Override
    @Transactional
    public String updateFile(FileUpdateDto fileUpdateDto) {
        DocFile docFile = getFileById(fileUpdateDto.getId());
        docFile.setStatus("CONFIRMED");
        fileRepository.save(docFile);
        if (Objects.nonNull(fileUpdateDto.getFileContentFieldList()) && !fileUpdateDto.getFileContentFieldList().isEmpty()) {
            for (FileContentFieldUpdateDto fileContentFieldUpdateDto : fileUpdateDto.getFileContentFieldList()) {
                fileContentFieldService.update(fileContentFieldUpdateDto);
            }
        }
        sendNotification(Notification.builder()
                .type(NotificationType.DATA_CONFIRMED.toString())
                .fileId(docFile.getId())
                .folderId(docFile.getFolder().getId())
                .status(Boolean.TRUE)
                .build());

        return "Successfully updated file";
    }

    @Override
    public List<DocFile> findByDocType(String notDetected, Integer docDetectionLimit) {
        return fileRepository.findByDocTypeAndLimit(notDetected, docDetectionLimit);
    }

    @Override
    @Transactional
    public void doBulkDocTypeDetection(List<DocFile> docFiles) throws Exception {
        for (DocFile docFile : docFiles) {
            Long userid = fileRepository.getUserIdByFileId( docFile.getId());
            List<String> alldocNameByUserId = docTypeUserWiseRepository.getApprovedDoctypenamesByUserId(userid);
            String docTypeDetectResponse = apiGatewayManager
                    .getDocTypeDetectResponse(docFile.getLocation(), docFile.getFileName());
            log.info("********* Anirban ML Doctype reposne {} ", docTypeDetectResponse);
            String docTypeDetectResponseFinal = getValidDopctypeNameByUser(alldocNameByUserId, docTypeDetectResponse);
            log.info("Anirban ML Doctype reposne {} Anirban DocType response: {}", docTypeDetectResponse, docTypeDetectResponseFinal);
            docFile.setDocType(docTypeDetectResponseFinal);
            sendNotification(Notification.builder()
                    .type(NotificationType.DETECTION.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.TRUE)
                    .build());
            fileRepository.save(docFile);
        }
    }

    @Override
    public DocFile detectDocType(DocFile docFile) throws Exception {
        //old
       /* String docTypeDetectResponse = apiGatewayManager
                .getDocTypeDetectResponse(docFile.getLocation(), docFile.getFileName());*/
        //new
        Long userid = fileRepository.getUserIdByFileId( docFile.getId());
        System.out.println("Anirban user id"+userid);
        List<String> alldocNameByUserId = docTypeUserWiseRepository.getApprovedDoctypenamesByUserId(userid);
        String docTypeDetectResponse = apiGatewayManager
                .getDocTypeDetectResponse(docFile.getLocation(), docFile.getFileName());
        docTypeDetectResponse = getValidDopctypeNameByUser(alldocNameByUserId, docTypeDetectResponse);
        log.info("DocType response: {}", docTypeDetectResponse);
        docFile.setDocType(docTypeDetectResponse);
        return fileRepository.save(docFile);
    }

    @Override
    @Transactional
    public void setFileStatusBulkEnqueue(List<Long> ids) {
        List<DocFile> docFiles = fileRepository.findAllById(ids);
        for (DocFile docFile : docFiles) {
            if (docFile.getStatus().equalsIgnoreCase("OCR FINISHED") ||
                    docFile.getStatus().equalsIgnoreCase("CONFIRMED")) {
                continue;
            }
            docFile.setStatus("ENQUEUE");
            fileRepository.save(docFile);
        }

    }

    @Override
    public Long getAverageOcrTime() {
        Long averageTime = fileRepository.getAverageTime();
        return (Objects.isNull(averageTime)) ? 0 : averageTime;
    }

  /*  @Override
    public DocFile fileClassifierConfirmation(Long id, String docType) {
        DocFile docFile = getFileById(id);
        docFile.setStatus("CONFIRM");
        String mlDocType = docFile.getDocType();
        if (!docType.isBlank()) {
            docFile.setDocType(docType);
        }
        if (!mlDocType.equals(docType)) {
            apiGatewayManager.postRequestToParserAnnotatorClassifier(ParserAnnotatorFileInfoDto.builder()
                    .filelocation(localServerProperties.getLocalServerURL() + "/api/v1/files/parser/" + docFile.getId())
                    .initialdoctype(docType.toLowerCase())
                    .mldoctype(mlDocType.toLowerCase())
                    .filename(docFile.getFileName())
                    .filetype(docFile.getFileType())
                    .build());
        }
        docFile = fileRepository.save(docFile);

        sendNotification(Notification.builder()
                .type(NotificationType.FILE_CLASSIFICATION_CONFIRMED.toString())
                .fileId(docFile.getId())
                .folderId(docFile.getFolder().getId())
                .status(Boolean.FALSE)
                .build());
        return docFile;
    }*/

    @Override
    //@Transactional
    public String delete(Long id) {
        if (fileRepository.getCountFileByFileIDDB(id)>0) {
            if (fileRepository.checkLocationOfFilesByFileid(id)>0) {
                DocFile docFile = getFileById(id);
                Folder folder = folderService.findById(docFile.getFolder().getId());
                String filePath = getFolderPathByTraversing(folder);
                //System.out.println("Anirban File path " + filePath);
                //fileRepository.delete(docFile);
                //fileRepository.deleteFilesById(id);
                Path path = Paths.get(rootPath.concat(File.separator).concat(filePath).concat(File.separator).concat(docFile.getKeyFileName()));
                File pathFile = new File(path.toString());

                try {
                    if (pathFile.exists()) {
                       // System.out.println("Anirban *** path " + path.toString());
                        Files.delete(path);
                    }
                } catch (IOException e) {
                    //System.out.println("Anirban Exception");
                    throw new OcrDmsException("File Delete Failed.");
                }
            }


            //System.out.println("Anirban " + id);
            fileRepository.deleteFilesByIdDB(id);
            //System.out.println("Anirban 2---" + id);
        }
        //old delete case
        //fileRepository.delete(docFile);
        return "Delete Successfully.";
    }

    @Override
    public List<SearchResponseDto> search(String value, List<Long> userIds) {
        List<DocFile> docFiles = fileRepository.search(value, userIds);
        if (!docFiles.isEmpty()){
            return docFiles.stream().map(file -> SearchResponseDto.builder()
                            .id(file.getId())
                            .content(file.getFileName())
                            .type("FILE").build())
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public ReRunResponse doReRunOCR(Integer fileid, String doctype,Integer doctypeid) throws ParseException {

        BulkFileDoOcr bulkFileDoOcr = new BulkFileDoOcr();
        bulkFileDoOcr.setFileId(fileid.longValue());
        bulkFileDoOcr.setDoOcr(Boolean.FALSE);
        //bulkFileDoOcr.setCreatedBy(currentuser);

        if (bulkFileDoOcrRepository.checkAlreadyPickeed(fileid.longValue(),Boolean.FALSE) <=0) {
            bulkFileDoOcrRepository.save(bulkFileDoOcr);
        }

       // try {
            ReRunResponse reRunResponse = new ReRunResponse();
            ArrayList<DoctypeAllAnnotator> root = new ArrayList<DoctypeAllAnnotator>();
            String response = apiGatewayManager.getAllPages();
            Gson gson = new Gson();
            DoctypeAllAnnotator[] rootAPIResponse = gson.fromJson(response, DoctypeAllAnnotator[].class);
            //String response = apiGatewayManager.getAllPages();
            //JsonObject convertedObject = new Gson().fromJson( response, JsonObject.class);
            //new Gson().fromJson(apiGatewayManager.getAllPages(), List<DoctypeAllAnnotator>.class);
            System.out.println("data size" +  rootAPIResponse.length);
            String doctypeocrstatus = "";
            for (Integer i = 0; i <  rootAPIResponse.length; i++) {
                if ( rootAPIResponse[i].getName().toUpperCase().equals(doctype.toUpperCase())) {
                    if ( rootAPIResponse[i].getOcrstatus() != null) {
                        doctypeocrstatus =  rootAPIResponse[i].getOcrstatus();
                    }
                }
            }
            if (doctypeocrstatus == null) {
            } else {
                if (doctypeocrstatus.equals("no")) {
                    System.out.println("API" + response);
                    reRunResponse.setStatus("success");
                    reRunResponse.setMessage("Not OCR status");
                } else {
                    reRunResponse.setStatus("success");
                    reRunResponse.setMessage("Done");
                    System.out.println("run ocr");
                    runOcrWithoutMultipleAttemptCheck(Long.parseLong(fileid.toString()), doctype, doctypeid);
                }
            }
            System.out.println("API Anirban" + response);
            return reRunResponse;
      /*  }catch (Exception ex){

            ex.printStackTrace();
            System.out.println("API Anirban error");

            return null;
        }*/

    }
    @Override
    public List<Long> getFileIdListByStatus(Long folderId, String status) {
        List<Long> files = fileRepository.getAllFileIdListByStatus(folderId, status);
        List<Folder> folderList = folderService.getFoldersByParentFolderId(folderId);
        traverseFolders(folderList, files, status);
        return files;
    }


    @Override
    public List<Long> getFileIdListByStatusDoctypeList(Long folderId, String status, ConfirmOCRFinishDoctypeListReq confirmOCRFinishDoctypeListReq) {
        List<Long> files = new ArrayList<Long>();
        if (confirmOCRFinishDoctypeListReq.getDoctypes().size() > 0) {
            files = fileRepository.getAllFileIdListByStatusDoctype(folderId, status, confirmOCRFinishDoctypeListReq.getDoctypes());
        }else {
            files = fileRepository.getAllFileIdListByStatus(folderId, status);
        }
        List<Folder> folderList = folderService.getFoldersByParentFolderId(folderId);
        traverseFolders(folderList, files, status);
        return files;
    }

    @Override
    public List<Long> getFileIdListByStatusDoctypeListByStatus(Long folderId, String status, ConfirmStatusDoctypeListReq confirmStatusDoctypeListReq) {
        List<Long> files = new ArrayList<Long>();
        if (confirmStatusDoctypeListReq.getDoctypes().size() > 0) {
            files = fileRepository.getAllFileIdListByStatusDoctype(folderId, status, confirmStatusDoctypeListReq.getDoctypes());
        }else {
            files = fileRepository.getAllFileIdListByStatus(folderId, status);
        }
        List<Folder> folderList = folderService.getFoldersByParentFolderId(folderId);
        traverseFolders(folderList, files, status);
        return files;
    }




    public void traverseFolders(List<Folder> folderList, List<Long> notConfirmedFileIds, String status) {
        if (folderList.isEmpty()) {
            return;
        }
        for (Folder folder : folderList) {
            notConfirmedFileIds.addAll(fileRepository.getAllFileIdListByStatus(folder.getId(), status));
            List<Folder> traverseFolderList = folderService.getFoldersByParentFolderId(folder.getId());
            traverseFolders(traverseFolderList, notConfirmedFileIds, status);
        }
    }

    public static String getFileExtension(String name) {
        String extension;
        try {
            extension = name.substring(name.lastIndexOf("."));

        } catch (Exception e) {
            extension = "";
        }
        return extension;
    }

    @Override
    public  String getFileExtensionMod (String name) {
        String extension;
        try {
            extension = name.substring(name.lastIndexOf("."));

        } catch (Exception e) {
            extension = "";
        }
        return extension;
    }

    private void sendNotification(Notification notification) {
        notificationService.sendMessagesNotification(notification);
    }

    @Override
    public DocFile fileClassifierConfirmation(Long id, String docType){
        //try {
        DocFile file = getFileById(id);
        file.setStatus("CONFIRM");
        String mlDocType = file.getDocType();
        if (!docType.isBlank()) {
            file.setDocType(docType);
        }
        if (!mlDocType.equals(docType)) {
            apiGatewayManager.postRequestToParserAnnotatorClassifier(ParserAnnotatorFileInfoDto.builder()
                    .filelocation(localServerProperties.getLocalServerURL() + "/api/v1/files/parser/" + file.getId())
                    .initialdoctype(docType.toLowerCase())
                    .mldoctype(mlDocType.toLowerCase())
                    .filename(file.getFileName())
                    .filetype(file.getFileType())
                    .build());
        }

        notificationService.sendMessagesNotification(Notification.builder()
                .fileId(file.getId())
                .folderId(file.getFolder().getId())
                .type(NotificationType.FILE_CLASSIFICATION_CONFIRMED.toString())
                .status(Boolean.TRUE)
                .build());

        //System.out.println("Anirban "+file.);
        return fileRepository.save(file);

     /*}catch (Exception ex){

         ex.printStackTrace();
         return null;
     }*/

    }
    @Override
    public List<FileContentField> runOcrWithDoctypeAndID(Long id, String doctype, Integer doctypeid) throws ParseException {
        DocFile docFile = getFileById(id);
        if (docFile.getStatus().equalsIgnoreCase("OCR FINISHED") ||
                docFile.getStatus().equalsIgnoreCase("CONFIRMED")) {
            throw new OcrDmsException("File already processed!", HttpStatus.BAD_REQUEST);
        }
        docFile.setStatus("RUNNING");
        fileRepository.save(docFile);
        LocalTime start = LocalTime.now();
        DocType docType = docTypeService.findDocTypeByNameForBulkOcr(docFile.getDocType());
        if (Objects.isNull(docType)) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            throw new OcrDmsException("DocType not found", HttpStatus.NOT_FOUND);
        }
        Map<String, String> map = null;
        try {
            map = apiGatewayManager.getRunOcrResponse(docFile.getLocation(), docFile.getDocType());
        } catch (Exception e) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            log.error("Error: ", e.getMessage());
            throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(map) || map.keySet().isEmpty()) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
        }
        //saveRunOcr(Map<String, String> map, Integer  doctypeid, Integer fileid)
        fileContentFieldService.saveRunOcr(map, doctypeid, Integer.parseInt(id.toString()));
        docFile.setStatus("OCR FINISHED");
        docFile.setDoOcrDurationInSec(Duration.between(start, LocalTime.now()).getSeconds());
        sendNotification(Notification.builder()
                .type(NotificationType.OCR.toString())
                .fileId(docFile.getId())
                .folderId(docFile.getFolder().getId())
                .status(Boolean.TRUE)
                .build());
        fileRepository.save(docFile);
        return fileContentFieldService.getFieldContentFieldByFile(docFile);
    }


    @Override
    @Transactional
    public void setFileStatusBulkErrorEnqueue(List<Long> ids) {
        List<DocFile> docFiles = fileRepository.findAllById(ids);
        for (DocFile docFile : docFiles) {
            if (docFile.getStatus().equalsIgnoreCase("ERROR DETECTION FINISHED") ||
                    docFile.getStatus().equalsIgnoreCase("CONFIRMED")) {
                continue;
            }
            System.out.println("Anirban Updating ENQUEUE ERROR");
            docFile.setStatus("ENQUEUE_ERROR");
            fileRepository.save(docFile);
            /*sendNotification(Notification.builder()
                    .type(NotificationType.ENQUEUE_ERROR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());*/
        }// end for
    }// end function

    @Override
    public void doBulkErrorDetection(DocFile docFile) throws ParseException {
        if (docFile == null){

            System.out.println("Doctype NULL");
        }else {


            if (docFile.getStatus().equalsIgnoreCase("ERROR DETECTION FINISHED") ||
                    docFile.getStatus().equalsIgnoreCase("CONFIRMED")) {
                return;
            }
            System.out.println("Anirban File status" + docFile.getStatus() + " id" + docFile.getId());
            docFile.setStatus("RUNNING_ERROR");
            fileRepository.save(docFile);
            LocalTime start = LocalTime.now();
            //old
            //DocType docType = docTypeService.findDocTypeByNameForBulkOcr(docFile.getDocType());
            //DocType docType = docTypeService.findDocTypeByNameForBulkOcr(docFile.getDocType());
            /*********************/
            DocType docType = new DocType();
            List<DocTypeAllResponse> apiInfo = apiGatewayManager.getAllPagesBYDoctype();
            for (Integer counter = 0; counter < apiInfo.size(); counter++) {

                System.out.println("Anirban provided " + docFile.getDocType().toLowerCase() + " from API " + apiInfo.get(counter).getName().toLowerCase());
                if (docFile.getDocType().toLowerCase().equals(apiInfo.get(counter).getName().toLowerCase())) {
                    docType.setId(apiInfo.get(counter).getId());
                    docType.setName(apiInfo.get(counter).getName());
                    docType.setOcrstatus(apiInfo.get(counter).getOcrstatus());
                    if (apiInfo.get(counter).getMultipagestatus().equals("yes")) {
                        docType.setIsMultiPageDoc(true);
                    } else {
                        docType.setIsMultiPageDoc(false);
                    }
                    //return docType1;
                }
            }// end for
            /******************/

            if (/*Objects.isNull(docType)*/ docType.getName() == null) {
                docFile.setStatus("FAILED");
                sendNotification(Notification.builder()
                        .type(NotificationType.ERROR_DETECTED.toString())
                        .fileId(docFile.getId())
                        .folderId(docFile.getFolder().getId())
                        .status(Boolean.FALSE)
                        .build());
                fileRepository.save(docFile);
                return;
            }
            Boolean haserror = false;
            List<FileContentField> fileContentFieldList = new ArrayList<FileContentField>();

            fileContentFieldList = fileContentFieldRepository.getFileContentDataByFileIdDB(docFile.getId());

            if (fileContentFieldList.size() > 0) {

                for (Integer counter3 = 0; counter3 < fileContentFieldList.size(); counter3++) {

                    if (
                            (
                                    fileContentFieldList.get(counter3).getType().equalsIgnoreCase("String") &&
                                            fileContentFieldList.get(counter3).getOcrValue().equals("")
                            )
                                    ||
                                    (
                                            fileContentFieldList.get(counter3).getType().equalsIgnoreCase("Number") &&
                                                    systemDateUtil.checkNumericFromString(fileContentFieldList.get(counter3).getOcrValue()) == false
                                    )
                                    ||
                                    (
                                            fileContentFieldList.get(counter3).getType().equalsIgnoreCase("Date") &&
                                                    systemDateUtil.getNewDateFormatFromUser(fileContentFieldList.get(counter3).getOcrValue()).equals("")
                                    )
                    ) {

                        haserror = true;
                    }

              /*  if ( fileContentFieldList.get(counter3).getType().equalsIgnoreCase("Number") && systemDateUtil.getNewDateFormatFromUser( fileContentFieldList.get(counter3).getOcrValue()).equals("")   ){

                    haserror = true;
                }*/

                }// end for

            }

       /* Map<String, String> map = null;
        try {
            map = apiGatewayManager.getRunOcrResponse(docFile.getLocation(), docFile.getDocType());
        } catch (Exception e) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            log.error("Error: ", e.getMessage());
            throw new OcrDmsException("Error detection failed!", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(map) || map.keySet().isEmpty()) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            throw new OcrDmsException("Error detection failed!", HttpStatus.BAD_REQUEST);
        }*/
       /* if (Objects.isNull(map) || map.keySet().isEmpty()) {
            docFile.setStatus("FAILED");
            sendNotification(Notification.builder()
                    .type(NotificationType.OCR.toString())
                    .fileId(docFile.getId())
                    .folderId(docFile.getFolder().getId())
                    .status(Boolean.FALSE)
                    .build());
            fileRepository.save(docFile);
            return;
        }*/
            //log.info("File Content Response File Id({}): {}", docFile.getId(), map);
            //fileContentFieldService.save(map, docFile, docType);
            if (haserror == true) {

                docFile.setStatus("ERROR DETECTED");
                docFile.setDoOcrDurationInSec(Duration.between(start, LocalTime.now()).getSeconds());
                sendNotification(Notification.builder()
                        .type(NotificationType.ERROR_DETECTED.toString())
                        .fileId(docFile.getId())
                        .folderId(docFile.getFolder().getId())
                        .status(Boolean.TRUE)
                        .build());
                fileRepository.save(docFile);


            } else {
                docFile.setStatus("ERROR DETECTION FINISHED");
                docFile.setDoOcrDurationInSec(Duration.between(start, LocalTime.now()).getSeconds());
                sendNotification(Notification.builder()
                        .type(NotificationType.ERROR_DETECTION_FINISHED.toString())
                        .fileId(docFile.getId())
                        .folderId(docFile.getFolder().getId())
                        .status(Boolean.TRUE)
                        .build());
                fileRepository.save(docFile);
            }
        }
    }

    public String getValidDopctypeNameByUser(List<String> doctypename, String doctypenameFromML){
        if (doctypename.size() > 0) {
            System.out.println("doctypename list size"+doctypename.size());
            System.out.println("doctypename from ML"+doctypenameFromML);
            for (Integer counter = 0; counter < doctypename.size(); counter++) {
                System.out.println("before Matched from DB"+doctypename.get(counter).toString());
                if (doctypename.get(counter).toString().equalsIgnoreCase(doctypenameFromML)) {
                    System.out.println("Matched doctypename from ML"+doctypenameFromML);
                    System.out.println("Matched from DB"+doctypename.get(counter).toString());
                    return doctypenameFromML;
                } /*else {
                    return "others";
                }*/
            }
            return "others";
        }else{
            //old
            //return "";
            return "others";
        }

    }// end function

}
