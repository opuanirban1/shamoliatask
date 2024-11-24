package com.dev.aes.repository;

import com.dev.aes.entity.DocTypeObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocTypeObjectRepository extends JpaRepository<DocTypeObject, Long>
{
}
