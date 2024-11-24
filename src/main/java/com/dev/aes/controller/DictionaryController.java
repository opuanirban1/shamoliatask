package com.dev.aes.controller;

import com.dev.aes.entity.Dictionary;
import com.dev.aes.payloads.request.DictionaryRequestDto;
import com.dev.aes.service.DictionaryService;
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
@RequestMapping("/api/v1/dictionary")
public class DictionaryController {
    private final DictionaryService service;

    @Autowired
    public DictionaryController(DictionaryService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<Dictionary> uploadFile(@Valid @RequestBody DictionaryRequestDto dto) {
        return new ResponseEntity<>(service.create(dto), HttpStatus.CREATED);
    }
}
