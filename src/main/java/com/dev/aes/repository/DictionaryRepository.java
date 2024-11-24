package com.dev.aes.repository;

import com.dev.aes.entity.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {
    List<Dictionary> findAllByUserId(Long userId);
}
