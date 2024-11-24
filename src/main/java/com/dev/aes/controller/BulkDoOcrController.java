package com.dev.aes.controller;

import com.dev.aes.payloads.request.FileIds;
import com.dev.aes.service.BulkFileDoOcrService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/bulkDoOcr")
public class BulkDoOcrController {
    private final BulkFileDoOcrService bulkFileDoOcrService;

    @Autowired
    public BulkDoOcrController(BulkFileDoOcrService bulkFileDoOcrService) {
        this.bulkFileDoOcrService = bulkFileDoOcrService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<String> uploadFile(@Valid @RequestBody FileIds fileIds) {
        return new ResponseEntity<>(bulkFileDoOcrService.create(fileIds), HttpStatus.CREATED);
    }
}
