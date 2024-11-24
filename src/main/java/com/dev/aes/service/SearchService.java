package com.dev.aes.service;

import com.dev.aes.payloads.response.SearchResponseDto;

import java.util.List;

public interface SearchService {
    List<SearchResponseDto> findByContent(String value);
}
