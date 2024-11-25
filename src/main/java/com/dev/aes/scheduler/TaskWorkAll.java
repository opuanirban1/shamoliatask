package com.dev.aes.scheduler;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableAsync
public class TaskWorkAll {
    /*public static Integer docDetectionLimit = 2;
    public static Integer runOcrLimit = 2;
    public static Integer doErrorDetectionLimit = 2;*/

    //@Autowired
   // private BulkFileDoOcrService bulkFileDoOcrService;

    //@Async
    //@Scheduled(cron = "*/3 * * ? * *")
   /* public void bulkDocTypeDetection() throws Exception {

        bulkFileDoOcrService.doBulkDocTypeDetectionAuth();
    }*/

   // @Async
    //@Scheduled(cron = "*/13 * * ? * *")
   /* public void bulkDoOCR() throws Exception {

        bulkFileDoOcrService.doBulkFileOcrAuth();
    }*/


    //@Async
    //@Scheduled(cron = "*/23 * * ? * *")
   /* public void bulkDoErrorDetection() throws Exception {
        bulkFileDoOcrService.doErrorDetection();
    }*/



}