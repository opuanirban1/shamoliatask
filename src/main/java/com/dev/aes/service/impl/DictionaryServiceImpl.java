package com.dev.aes.service.impl;

import com.dev.aes.entity.Dictionary;
import com.dev.aes.payloads.request.DictionaryRequestDto;
import com.dev.aes.repository.DictionaryRepository;
import com.dev.aes.service.DictionaryService;
import com.dev.aes.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DictionaryServiceImpl implements DictionaryService {
    private final DictionaryRepository repository;
    private final UserService userService;

    @Autowired
    public DictionaryServiceImpl(DictionaryRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public Dictionary create(DictionaryRequestDto dto) {
        return repository.save(Dictionary.builder().userId(userService.getCurrentuser().getId()).word(dto.getWord()).build());
    }

    @Override
    public Set<String> findUserCorrectionWords() {
        Long userId = userService.getCurrentuser().getId();
        List<Dictionary> dictionaries = repository.findAllByUserId(userId);
        return dictionaries.stream().map(Dictionary::getWord).collect(Collectors.toSet());
    }
}
