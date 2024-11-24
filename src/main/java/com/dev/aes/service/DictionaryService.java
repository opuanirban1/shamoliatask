package com.dev.aes.service;

import com.dev.aes.entity.Dictionary;
import com.dev.aes.payloads.request.DictionaryRequestDto;

import java.util.Set;

public interface DictionaryService {
    Dictionary create(DictionaryRequestDto dto);

    Set<String> findUserCorrectionWords();
}
