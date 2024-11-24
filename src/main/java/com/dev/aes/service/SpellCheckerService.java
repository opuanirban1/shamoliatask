package com.dev.aes.service;

import com.dev.aes.payloads.response.SpellCheckResponse;

public interface SpellCheckerService {
    SpellCheckResponse checkSpell(String content);
}
