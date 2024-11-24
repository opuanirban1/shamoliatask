package com.dev.aes.repository;

import com.dev.aes.entity.DocFile;
import com.dev.aes.entity.FileContentField;
import com.dev.aes.entity.Folder;
import com.dev.aes.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileContentFieldRepository extends JpaRepository<FileContentField, Long> {
    List<FileContentField> findByFileIdOrderBySequence(Long fileId);

    @Query(value = "SELECT * FROM file_content_field f WHERE f.ocr_value LIKE %:name% AND f.created_by IN :userIds", nativeQuery = true)
    List<FileContentField> search(@Param("name") String name, @Param("userIds") List<Long> userIds);

   /* @Query(value = "SELECT * FROM file_content_field f WHERE f.ocr_value LIKE %:searchString% AND f.file_id IN :fileIds", nativeQuery = true)
    List<FileContentField> searchByFileIds(@Param("searchString") String searchString, @Param("fileIds") List<Long> fileIds);*/

    @Query(value = "SELECT * FROM file_content_field f WHERE f.ocr_value LIKE %:searchString% AND f.file_id IN :fileIds", nativeQuery = true)
    List<FileContentField> searchByFileIds(@Param("searchString") String searchString, @Param("fileIds") List<Long> fileIds);


    @Query(value = "select * from file_content_field where file_id =?1", nativeQuery = true)
    List<FileContentField> getFileContentDataByFileIdDB(Long id);

    @Modifying
    @Transactional
    @Query(value="delete from file_content_field where file_id= :fileid and doctype_id = :doctypeid", nativeQuery = true)
    void deleteInfoFromfileContentField (@Param("fileid") Long fileid, @Param("doctypeid") Long doctypeid);

    @Modifying
    @Transactional
    @Query(value="delete from file_content_field where file_id= :fileid", nativeQuery = true)
    void deleteInfoFromfileContentFieldByFileID (@Param("fileid") Long  fileid);


    @Modifying
    @Transactional
    @Query(value = "insert into file_content_field (create_at,doctype_field_id,doctype_id,file_id,name,ocr_value,sequence,type,update_at,created_by,language,doctypemainclassid,mapkey) " +
            "values (now(), :doctypefieldid, :doctypeid, :fileid, :name, :ocrvalue,:sequence, :type, null, :createdby, :language, 0,  :mapkey)", nativeQuery = true)
    void insertFileContentData (@Param("name") String name,
                                @Param("ocrvalue") String ocrvalue,
                                @Param("doctypeid") Long doctypeid,
                                @Param("doctypefieldid") Long doctypefieldid,
                                @Param("type") String type,
                                @Param("sequence") Integer  sequence,
                                @Param("language") String language,
                                @Param("fileid") Long fileid,
                                @Param("createdby") Long createdby,
                                @Param("mapkey") String mapkey
                                );

}
