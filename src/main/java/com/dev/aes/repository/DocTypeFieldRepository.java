package com.dev.aes.repository;

import com.dev.aes.entity.DocTypeField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocTypeFieldRepository extends JpaRepository<DocTypeField, Long> {

    Long countByDocTypeId(Long id);
    List<DocTypeField> findAllByDocTypeId(Long id);
    List<DocTypeField> findAll(Specification<DocTypeField> specification);
}
