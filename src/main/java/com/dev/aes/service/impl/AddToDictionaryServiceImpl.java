package com.dev.aes.service.impl;

import com.dev.aes.config.APIGatewayManager;
import com.dev.aes.entity.AddToDictionary;
import com.dev.aes.entity.DocFile;
import com.dev.aes.entity.User;
import com.dev.aes.payloads.request.AddToDictionaryDto;
import com.dev.aes.payloads.request.AddToDictionaryParserAnnotatorDto;
import com.dev.aes.repository.AddToDictionaryRepository;
import com.dev.aes.service.AddToDictionaryService;
import com.dev.aes.service.FileService;
import com.dev.aes.service.UserService;
import com.dev.aes.util.LocalServerProperties;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddToDictionaryServiceImpl implements AddToDictionaryService {
    private final AddToDictionaryRepository repository;
    private final UserService userService;
    private final FileService fileService;
    private final APIGatewayManager apiGatewayManager;
    private final LocalServerProperties localServerProperties;

    @Autowired
    public AddToDictionaryServiceImpl(AddToDictionaryRepository repository,
                                      UserService userService,
                                      FileService fileService,
                                      APIGatewayManager apiGatewayManager, LocalServerProperties localServerProperties) {
        this.repository = repository;
        this.userService = userService;
        this.fileService = fileService;
        this.apiGatewayManager = apiGatewayManager;
        this.localServerProperties = localServerProperties;
    }

    @Transactional
    @Override
    public AddToDictionary create(AddToDictionaryDto dto) {
        DocFile docFile = fileService.getFileById(dto.getFileId());
        User currentUser = userService.getCurrentuser();
        AddToDictionary saveEntity = repository.save(AddToDictionary.builder()
                .correctWord(dto.getCorrectWord())
                .wrongWord(dto.getWrongWord())
                .fileId(dto.getFileId())
                .approvalStatus(Boolean.FALSE)
                .userId(currentUser.getId()).build());
        apiGatewayManager.postRequestToParserAnnotatorAddToDictionary(AddToDictionaryParserAnnotatorDto.builder()
                .id(saveEntity.getId())
                .userId(saveEntity.getUserId())
                .correctWord(saveEntity.getCorrectWord())
                .wrongWord(saveEntity.getWrongWord())
                .fileName(docFile.getFileName())
                .fileType(docFile.getFileType())
                .fileUrl(localServerProperties.getLocalServerURL() + "/api/v1/files/parser/" + docFile.getId())
                .location(docFile.getLocation())
                .build());
        return saveEntity;
    }
}
