package com.dev.aes.service;

import com.dev.aes.entity.AddToDictionary;
import com.dev.aes.payloads.request.AddToDictionaryDto;

public interface AddToDictionaryService {
    AddToDictionary create(AddToDictionaryDto dto);
}
