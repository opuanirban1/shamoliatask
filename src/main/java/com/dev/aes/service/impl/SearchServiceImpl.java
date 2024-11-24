package com.dev.aes.service.impl;

import com.dev.aes.entity.User;
import com.dev.aes.payloads.response.SearchResponseDto;
import com.dev.aes.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {
    private final UserService userService;
    private final FileService fileService;
    private final FolderService folderService;
    private final FileContentFieldService fileContentFieldService;

    @Autowired
    public SearchServiceImpl(UserService userService, FileService fileService,
                             FolderService folderService, FileContentFieldService fileContentFieldService) {
        this.userService = userService;
        this.fileService = fileService;
        this.folderService = folderService;
        this.fileContentFieldService = fileContentFieldService;
    }

    @Override
    public List<SearchResponseDto> findByContent(String value) {
        List<User> users = userService.getUsersForSearch();
        if (users.isEmpty()) {
            return List.of();
        }
        List<Long> userIds = users.stream().map(User::getId).toList();
        List<SearchResponseDto> allSearchResponse = new ArrayList<>();
        List<SearchResponseDto> folderSearchResponse = folderService.search(value, userIds);
        List<SearchResponseDto> fileSearchResponse = fileService.search(value, userIds);
        List<SearchResponseDto> fileContentSearchResponse = fileContentFieldService.search(value, userIds);
        allSearchResponse.addAll(folderSearchResponse);
        allSearchResponse.addAll(fileSearchResponse);
        allSearchResponse.addAll(fileContentSearchResponse);
        return allSearchResponse;
    }
}
