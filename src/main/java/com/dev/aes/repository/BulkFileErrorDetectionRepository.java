package com.dev.aes.repository;

import com.dev.aes.entity.BulkFileDoOcr;
import com.dev.aes.entity.BulkFileErrorDetection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkFileErrorDetectionRepository extends JpaRepository <BulkFileErrorDetection, Long> {

    @Transactional
    @Modifying
    @Query(value="insert into bulk_file_error_detection (file_id, errorcount, status, created_at) values (:fileid, :errorcount, :status, now())", nativeQuery = true)
    void insertBulkErrorDetection (@Param("fileid") Long fileid, @Param("errorcount") Integer errorcount, @Param("status") String status);

    @Query(value="select * from bulk_file_error_detection where status = :status limit :limitvalue", nativeQuery = true)
    List<BulkFileErrorDetection> getBulkErrorDetectionByStatus(@Param("status") String status, @Param("limitvalue") Integer limitvalue);

    @Transactional
    @Modifying
    @Query(value="update bulk_file_error_detection set status = :status where id = :id", nativeQuery = true)
    void updateBulkErrorDetectionById (@Param("status") String status, @Param("id") Long id);


    @Transactional
    @Modifying
    BulkFileErrorDetection save (BulkFileErrorDetection bulkFileErrorDetection);


    @Query(value="select ifnull(count(*),0) from bulk_file_error_detection where file_id =:fileid and status=:status and errorcount= :errorcount", nativeQuery = true)
    Long checkAlreadyIsertedInerrorDetect(@Param("fileid") Long fileid, @Param("status") String status, @Param("errorcount") Integer errorcount);
}
