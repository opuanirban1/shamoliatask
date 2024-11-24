package com.dev.aes.controller;

import com.dev.aes.payloads.request.InsertWord;
import com.dev.aes.payloads.response.SpellCheckResponse;
import com.dev.aes.service.SpellCheckerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/spell-check")
public class SpellCheckController {

    private final SpellCheckerService spellCheckerService;

    @Autowired
    public SpellCheckController(SpellCheckerService spellCheckerService) {
        this.spellCheckerService = spellCheckerService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<SpellCheckResponse> callBanglaSpellCheck(@Valid @RequestBody InsertWord insertWord) {
        return new ResponseEntity<>(spellCheckerService.checkSpell(insertWord.getContent()), HttpStatus.OK);
    }
}
