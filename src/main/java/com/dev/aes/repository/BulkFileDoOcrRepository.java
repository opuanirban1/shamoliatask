package com.dev.aes.repository;

import com.dev.aes.entity.BulkFileDoOcr;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkFileDoOcrRepository extends JpaRepository<BulkFileDoOcr, Long> {
    @Query(value="select * from bulk_file_do_ocr where do_ocr = ?1 limit ?2", nativeQuery=true)
    List<BulkFileDoOcr> findByDoOcrLimit(Boolean aFalse, Integer runOcrLimit);

    @Transactional
    @Modifying
    @Query(value="update bulk_file_do_ocr set do_ocr = :status where id = :id", nativeQuery = true)
    void updateBulkdDoOCRById (@Param("status") Boolean status, @Param("id") Long id);

    @Transactional
    @Modifying
    BulkFileDoOcr save (BulkFileDoOcr bulkFileDoOcr);


    @Query(value="select ifnull(count(*), 0) from bulk_file_do_ocr where do_ocr=:status and file_id = :fileid", nativeQuery = true)
    Long checkAlreadyPickeed(@Param("fileid") Long fileid, @Param("status") Boolean status);
}
