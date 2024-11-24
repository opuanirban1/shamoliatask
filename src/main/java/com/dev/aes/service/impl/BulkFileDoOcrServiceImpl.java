package com.dev.aes.service.impl;

import com.dev.aes.entity.BulkFileDoOcr;
import com.dev.aes.entity.BulkFileErrorDetection;
import com.dev.aes.entity.DocFile;
import com.dev.aes.entity.User;
import com.dev.aes.exception.OcrDmsException;
import com.dev.aes.payloads.request.FileIds;
import com.dev.aes.repository.BulkFileDoOcrRepository;
import com.dev.aes.repository.BulkFileErrorDetectionRepository;
import com.dev.aes.repository.FileRepository;
import com.dev.aes.scheduler.TaskWorkAll;
import com.dev.aes.service.BulkFileDoOcrService;
import com.dev.aes.service.FileContentFieldService;
import com.dev.aes.service.FileService;
import com.dev.aes.service.UserService;
import com.dev.aes.util.SystemDateUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BulkFileDoOcrServiceImpl implements BulkFileDoOcrService {
    private final BulkFileDoOcrRepository bulkFileDoOcrRepository;
    private final FileService fileService;
    private final FileContentFieldService fileContentFieldService;
    private final FileRepository fileRepository;
    private final UserService userService;
    private final BulkFileErrorDetectionRepository bulkFileErrorDetectionRepository;
    private final SystemDateUtil systemDateUtil;

    @Autowired
    public BulkFileDoOcrServiceImpl(BulkFileDoOcrRepository bulkFileDoOcrRepository,
                                    FileService fileService,
                                    FileContentFieldService fileContentFieldService,
                                    FileRepository fileRepository, UserService userService,
                                    BulkFileErrorDetectionRepository bulkFileErrorDetectionRepository,
                                    SystemDateUtil systemDateUtil) {
        this.bulkFileDoOcrRepository = bulkFileDoOcrRepository;
        this.fileService = fileService;
        this.fileContentFieldService = fileContentFieldService;
        this.fileRepository = fileRepository;
        this.userService = userService;
        this.bulkFileErrorDetectionRepository = bulkFileErrorDetectionRepository;
        this.systemDateUtil = systemDateUtil;
    }

    @Override
    public void doBulkFileOcr() throws Exception {
        List<BulkFileDoOcr> bulkFileDoOcrList = bulkFileDoOcrRepository.findByDoOcrLimit(Boolean.FALSE, TaskWorkAll.runOcrLimit);
        if (bulkFileDoOcrList.isEmpty()) {
            return;
        }
        for (BulkFileDoOcr bulkFileDoOcr : bulkFileDoOcrList) {
            Optional<DocFile> optionalFile = fileRepository.findById(bulkFileDoOcr.getFileId());

            if (optionalFile.isEmpty()) {
                bulkFileDoOcr.setDoOcr(Boolean.TRUE);
                bulkFileDoOcrRepository.save(bulkFileDoOcr);
                continue;
            }
            DocFile docFile = optionalFile.get();
            if (docFile.getDocType().equals("NOT_DETECTED")){
                docFile = fileService.detectDocType(docFile);
            }
            try {
                fileService.doBulkOcr(docFile);
            } catch (Exception e) {
                bulkFileDoOcr.setDoOcr(Boolean.TRUE);
                bulkFileDoOcrRepository.save(bulkFileDoOcr);
                e.printStackTrace();
                throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
            }
            bulkFileDoOcr.setDoOcr(Boolean.TRUE);
            bulkFileDoOcrRepository.save(bulkFileDoOcr);
        }
    }

    @Override
    @Transactional
    public String create(FileIds fileIds) {
        if (Objects.isNull(fileIds) || fileIds.getIds() == null || fileIds.getIds().isEmpty()) {
            return "Saved";
        }
        User currentuser = userService.getCurrentuser();

        for (Long fileId : fileIds.getIds()) {
            BulkFileDoOcr bulkFileDoOcr = new BulkFileDoOcr();
            bulkFileDoOcr.setFileId(fileId);
            bulkFileDoOcr.setDoOcr(Boolean.FALSE);
            bulkFileDoOcr.setCreatedBy(currentuser);
            bulkFileDoOcrRepository.save(bulkFileDoOcr);
        }
        fileService.setFileStatusBulkEnqueue(fileIds.getIds());
        return "Saved";
    }

    @Override
    @Transactional
    public String createOCR(FileIds fileIds) {
        if (Objects.isNull(fileIds) || fileIds.getIds() == null || fileIds.getIds().isEmpty()) {
            return "Saved";
        }
        //User currentuser = userService.getCurrentuserByAuth();

        for (Long fileId : fileIds.getIds()) {
            BulkFileDoOcr bulkFileDoOcr = new BulkFileDoOcr();
            bulkFileDoOcr.setFileId(fileId);
            bulkFileDoOcr.setDoOcr(Boolean.FALSE);
            //bulkFileDoOcr.setCreatedBy(currentuser);

            if (bulkFileDoOcrRepository.checkAlreadyPickeed(fileId,Boolean.FALSE) <=0) {
                bulkFileDoOcrRepository.save(bulkFileDoOcr);
            }
        }
        fileService.setFileStatusBulkEnqueue(fileIds.getIds());
        return "Saved";
    }

    @Override
    public void doBulkDocTypeDetection() throws Exception {
        List<DocFile> docFiles = fileService.findByDocType("NOT_DETECTED", TaskWorkAll.docDetectionLimit);
        if (docFiles.isEmpty()) {
            return;
        }
        fileService.doBulkDocTypeDetection(docFiles);
        // insert into bulk ocr table
        FileIds fileIds = new FileIds();
        List<Long> fileids= new ArrayList<Long>();
        if (docFiles.size()>0){
            for (Integer count=0; count<docFiles.size(); count++){
                fileids.add(docFiles.get(count).getId());
            }
            fileIds.setIds(fileids);
            create(fileIds);
        }
    }

    @Override
    public void doBulkDocTypeDetectionAuth() throws Exception {

        List<DocFile> docFiles = fileService.findByDocType("NOT_DETECTED", TaskWorkAll.docDetectionLimit);
        if (docFiles.isEmpty()) {
            return;
        }
        fileService.doBulkDocTypeDetection(docFiles);
        // insert into bulk ocr table
        FileIds fileIds = new FileIds();
        List<Long> fileids= new ArrayList<Long>();
        if (docFiles.size()>0){
            for (Integer count=0; count<docFiles.size(); count++){
                fileids.add(docFiles.get(count).getId());
            }
            List<Long> fileIdExceptOthers = fileRepository.getFileIdByDocTypeExceptOthers(fileids);
            if (fileIdExceptOthers.size()>0){
                fileIds.setIds(fileIdExceptOthers);
                //create(fileIds);
                createOCR(fileIds);
            }
            //fileIds.setIds(fileids);
            //create(fileIds);
            //createOCR(fileIds);
        }
    }


     @Override
     public void doBulkFileOcrAuth() throws  Exception{

         List<BulkFileDoOcr> bulkFileDoOcrList = bulkFileDoOcrRepository.findByDoOcrLimit(Boolean.FALSE, TaskWorkAll.runOcrLimit);
         if (bulkFileDoOcrList.isEmpty()) {
             return;
         }
         for (BulkFileDoOcr bulkFileDoOcr : bulkFileDoOcrList) {
             Optional<DocFile> optionalFile = fileRepository.findById(bulkFileDoOcr.getFileId());

             if (optionalFile.isEmpty()) {
                 bulkFileDoOcr.setDoOcr(Boolean.TRUE);
                 bulkFileDoOcrRepository.save(bulkFileDoOcr);
                 continue;
             }
             DocFile docFile = optionalFile.get();
             if (docFile.getDocType().equals("NOT_DETECTED")){
                 docFile = fileService.detectDocType(docFile);
             }
             try {
                 fileService.doBulkOcr(docFile);
             } catch (Exception e) {
                 bulkFileDoOcr.setDoOcr(Boolean.TRUE);
                 //bulkFileDoOcrRepository.save(bulkFileDoOcr);

                 System.out.println("Updated by status"+bulkFileDoOcr.getDoOcr()+" id"+bulkFileDoOcr.getId());
                 if (bulkFileErrorDetectionRepository.checkAlreadyIsertedInerrorDetect(bulkFileDoOcr.getFileId(),"active", 0) <=0) {
                     bulkFileErrorDetectionRepository.insertBulkErrorDetection(bulkFileDoOcr.getFileId(), 0, "active");
                 }
                 bulkFileDoOcrRepository.updateBulkdDoOCRById(bulkFileDoOcr.getDoOcr(), bulkFileDoOcr.getId());
                 e.printStackTrace();
                 throw new OcrDmsException("File ocr failed!", HttpStatus.BAD_REQUEST);
             }
             bulkFileDoOcr.setDoOcr(Boolean.TRUE);
             //bulkFileDoOcrRepository.save(bulkFileDoOcr);

             System.out.println("Updated by status"+bulkFileDoOcr.getDoOcr()+" id"+bulkFileDoOcr.getId());
             if (bulkFileErrorDetectionRepository.checkAlreadyIsertedInerrorDetect(bulkFileDoOcr.getFileId(),"active", 0) <=0) {
                 bulkFileErrorDetectionRepository.insertBulkErrorDetection(bulkFileDoOcr.getFileId(), 0, "active");
             }
             bulkFileDoOcrRepository.updateBulkdDoOCRById(bulkFileDoOcr.getDoOcr(), bulkFileDoOcr.getId());
         }// end for
     }// end function


    @Override
    public void doErrorDetection() throws Exception{
        List<BulkFileErrorDetection> bulkFileErrorDetectionList = new ArrayList<BulkFileErrorDetection>();
        List<Long> longListFileids = new ArrayList<Long>();
        bulkFileErrorDetectionList = bulkFileErrorDetectionRepository.getBulkErrorDetectionByStatus("active", TaskWorkAll.doErrorDetectionLimit);

        if( bulkFileErrorDetectionList.size()>0){
            for (Integer counter1=0; counter1<bulkFileErrorDetectionList.size(); counter1++){
                longListFileids.add(bulkFileErrorDetectionList.get(counter1).getFileId());
            }
            fileService.setFileStatusBulkErrorEnqueue(longListFileids);
            for (Integer counter=0; counter<bulkFileErrorDetectionList.size(); counter++){

                DocFile docFile = fileRepository.findDocFileById(bulkFileErrorDetectionList.get(counter).getFileId());
                fileService.doBulkErrorDetection(docFile);
                bulkFileErrorDetectionRepository.updateBulkErrorDetectionById("processed", bulkFileErrorDetectionList.get(counter).getId());
            }// end for
        }// end if
    }// end function

}
