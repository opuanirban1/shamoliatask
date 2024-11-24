package com.dev.aes.repository;

import com.dev.aes.entity.AllEncryption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllencryptionRepository extends JpaRepository<AllEncryption, Long>{


    @Query(value="select * from allencryption where status='active'", nativeQuery = true)
    List<AllEncryption> getAllEncrytionData();
}

