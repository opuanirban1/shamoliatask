package com.dev.aes.controller;

import com.dev.aes.service.AddToDictionaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/addToDictionary")
public class AddToDictionaryController {
    private final AddToDictionaryService addToDictionaryService;

    @Autowired
    public AddToDictionaryController(AddToDictionaryService addToDictionaryService) {
        this.addToDictionaryService = addToDictionaryService;
    }

//    @PostMapping
//    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
//    public ResponseEntity<AddToDictionary> createFolder(@Valid @RequestBody AddToDictionaryDto dto) {
//        return new ResponseEntity<>(addToDictionaryService.create(dto), HttpStatus.CREATED);
//    }
}
