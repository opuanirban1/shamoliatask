package com.dev.aes.repository;

import com.dev.aes.entity.DocType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocTypeRepository extends JpaRepository<DocType, Long>
{
    boolean existsByName(String name);

    Optional<DocType> findByName(String name);


}
