package com.dev.aes.repository;

import com.dev.aes.entity.AddToDictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface AddToDictionaryRepository extends JpaRepository<AddToDictionary, Long> {
}
