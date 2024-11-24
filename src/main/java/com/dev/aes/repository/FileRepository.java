package com.dev.aes.repository;

import com.dev.aes.entity.DocFile;
import com.dev.aes.entity.DocType;
import com.dev.aes.entity.File;
import com.dev.aes.entity.Folder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface FileRepository extends JpaRepository<DocFile, Long> {
    Set<DocFile> findByFolder(Folder folder);
    @Query("SELECT AVG(f.doOcrDurationInSec) from DocFile f")
    Long getAverageTime();

    @Query(value="select * from files where doc_type = ?1 limit ?2", nativeQuery=true)
    List<DocFile> findByDocTypeAndLimit(String notDetected, Integer docDetectionLimit);

    boolean existsByFileNameAndFolderId(String originalFilename, Long folderId);

    boolean existsByFolderId(Long folderId);

    Long countByFileNameContainingAndFolderId(String originalFilename, Long folderId);

    @Query(value = "SELECT * FROM files f WHERE f.file_name LIKE %:name% AND f.created_by IN :userIds", nativeQuery = true)
    List<DocFile> search(@Param("name") String name, @Param("userIds") List<Long> userIds);

    void deleteAllByFolder(Folder folder);

    @Query(value = "select id from files where folder_id = :folderId and status = :status and doc_type  not in ('NOT_DETECTED') order by id desc", nativeQuery = true)
    List<Long> getAllFileIdListByStatus(@Param("folderId") Long folderId, @Param("status") String status);


    @Query(value = "select id from files where folder_id = :folderId and status = :status and doc_type  not in ('NOT_DETECTED') and doc_type in :doctypes order by id desc", nativeQuery = true)
    List<Long> getAllFileIdListByStatusDoctype (@Param("folderId") Long folderId, @Param("status") String status , @Param("doctypes") List<String> doctypes);


   /* @Query(value = "SELECT * FROM files f WHERE f.file_name LIKE %:name% AND f.folder_id IN :folderIds order by id desc", nativeQuery = true)
    List<DocFile> searchByFolderIds(@Param("name") String name, @Param("folderIds") List<Long> folderIds);*/

    @Query(value = "SELECT * FROM files f WHERE f.file_name LIKE %:name% AND f.folder_id IN :folderIds", nativeQuery = true)
    List<DocFile> searchByFolderIds(@Param("name") String name, @Param("folderIds") List<Long> folderIds);

    @Query( value="select count(*) from files where folder_id = :folderid", nativeQuery = true)
    Integer  getFileByDoctypePaginationTotalCountNULL ( @Param("folderid") Long folderid );

    @Query( value="select count(*) from files where folder_id = :folderid and status = :status ", nativeQuery = true)
    Integer  getFileByDoctypePaginationTotalCountNULLErrorDetect ( @Param("folderid") Long folderid, @Param("status") String status );

    @Query( value="" +
            " SELECT k.id," +
            "k.created_at," +
            "k.do_ocr_duration_in_sec," +
            "k.doc_type," +
            "k.file_name, k.key_file_name," +
            "k.file_type," +
            "k.location," +
            "k.status," +
            "k.updated_at," +
            "k.folder_id," +
            "k.created_by" +
            " FROM (SELECT p.id," +
            "p.created_at," +
            "p.do_ocr_duration_in_sec," +
            "p.doc_type," +
            "p.file_name, p.key_file_name," +
            "p.file_type," +
            "p.location," +
            "p.status," +
            "p.updated_at," +
            "p.folder_id," +
            "p.created_by," +
            "Row_Number() OVER (ORDER BY p.id desc) MyRow FROM files p where  p.folder_id = :folderid ORDER BY p.id desc ) k " +
            " WHERE MyRow BETWEEN :startlimit AND :endlimit ORDER BY k.id desc", nativeQuery = true)
    Set<DocFile> /*List<DocFile>*/ getFileByDoctypePaginationNUll (
            @Param("folderid") Long folderid,
            @Param("startlimit") Integer startlimit,
            @Param("endlimit") Integer endlimit );

    @Query( value="" +
            " SELECT k.id," +
            "k.created_at," +
            "k.do_ocr_duration_in_sec," +
            "k.doc_type," +
            "k.file_name, k.key_file_name," +
            "k.file_type," +
            "k.location," +
            "k.status," +
            "k.updated_at," +
            "k.folder_id," +
            "k.created_by" +
            " FROM (SELECT p.id," +
            "p.created_at," +
            "p.do_ocr_duration_in_sec," +
            "p.doc_type," +
            "p.file_name, p.key_file_name," +
            "p.file_type," +
            "p.location," +
            "p.status," +
            "p.updated_at," +
            "p.folder_id," +
            "p.created_by, " +
            "Row_Number() OVER (ORDER BY p.id desc) MyRow FROM files p where  p.folder_id = :folderid and p.status= :status ORDER BY p.id desc ) k " +
            " WHERE MyRow BETWEEN :startlimit AND :endlimit ORDER BY k.id desc", nativeQuery = true)
    Set<DocFile> /*List<DocFile>*/ getFileByDoctypePaginationNUllErrorDetect (
            @Param("folderid") Long folderid,
            @Param("startlimit") Integer startlimit,
            @Param("endlimit") Integer endlimit , @Param("status") String status);


    @Query( value="select count(*) from files where folder_id = :folderid and doc_type in (:doctypes)", nativeQuery = true)
    Integer  getFileByDoctypePaginationTotalCount (@Param("doctypes") List<String> doctypes, @Param("folderid") Long folderid );

    @Query( value="select count(*) from files where folder_id = :folderid and doc_type in (:doctypes) and status = :status ", nativeQuery = true)
    Integer  getFileByDoctypePaginationTotalCountErrorDetect (@Param("doctypes") List<String> doctypes, @Param("folderid") Long folderid , @Param("status") String status);

    @Query( value="" +
            " SELECT k.id," +
            "k.created_at," +
            "k.do_ocr_duration_in_sec," +
            "k.doc_type," +
            "k.file_name, k.key_file_name," +
            "k.file_type," +
            "k.location," +
            "k.status," +
            "k.updated_at," +
            "k.folder_id," +
            "k.created_by" +
            " FROM (SELECT p.id," +
            "p.created_at," +
            "p.do_ocr_duration_in_sec," +
            "p.doc_type," +
            "p.file_name, p.key_file_name," +
            "p.file_type," +
            "p.location," +
            "p.status," +
            "p.updated_at," +
            "p.folder_id," +
            "p.created_by,  " +
            "Row_Number() OVER (ORDER BY p.id desc) MyRow FROM files p where  upper(p.doc_type) in :doctypes and p.folder_id = :folderid and p.status = :status ORDER BY p.id desc) k " +
            " WHERE MyRow BETWEEN :startlimit AND :endlimit ORDER BY k.id desc", nativeQuery = true)
    Set<DocFile> /*List<DocFile>*/ getFileByDoctypePaginationErrorDetect (@Param("doctypes") List<String> doctypes,
                                                               @Param("folderid") Long folderid,
                                                               @Param("startlimit") Integer startlimit,
                                                               @Param("endlimit") Integer endlimit, @Param("status") String status );


    @Query( value="" +
            " SELECT k.id," +
            "k.created_at," +
            "k.do_ocr_duration_in_sec," +
            "k.doc_type," +
            "k.file_name, k.key_file_name," +
            "k.file_type," +
            "k.location," +
            "k.status," +
            "k.updated_at," +
            "k.folder_id," +
            "k.created_by" +
            " FROM (SELECT p.id," +
            "p.created_at," +
            "p.do_ocr_duration_in_sec," +
            "p.doc_type," +
            "p.file_name, p.key_file_name," +
            "p.file_type," +
            "p.location," +
            "p.status," +
            "p.updated_at," +
            "p.folder_id," +
            "p.created_by," +
            "Row_Number() OVER (ORDER BY p.id desc) MyRow FROM files p where  upper(p.doc_type) in :doctypes and p.folder_id = :folderid  ORDER BY p.id desc) k " +
            " WHERE MyRow BETWEEN :startlimit AND :endlimit ORDER BY k.id desc", nativeQuery = true)
    Set<DocFile> /*List<DocFile>*/ getFileByDoctypePagination (@Param("doctypes") List<String> doctypes,
                                          @Param("folderid") Long folderid,
                                          @Param("startlimit") Integer startlimit,
                                          @Param("endlimit") Integer endlimit );

    @Query( value = "select k.id from  files k where upper(k.doc_type) = :doctype and k.status = 'CONFIRM' and k.folder_id = :folderid  order by id desc", nativeQuery = true)
    List<Integer> getDBNextFileidByDTypeFolderID (@Param("doctype") String doctype, @Param("folderid") Integer folderid);

    @Modifying
    @Transactional
    @Query(value = "update files set status = :statustext, doc_type = :doctypename where id = :fileid", nativeQuery = true)
    void updateFilestatusByidandStatustext(@Param("statustext") String statustext, @Param("fileid") Integer fileid, @Param("doctypename") String doctypename);

    @Query(value = "select * from files where id = :fileid", nativeQuery = true)
    DocFile findDocFileById (@Param("fileid") Long fileid);

    @Query(value="select folder_id  from files f where id = :fileid", nativeQuery = true)
    Long getFolderIdByFileId (@Param("fileid") Long fileid);

    @Query(value = "select distinct a.folder_id from ( " +
            "       select k.folder_id  from files k where k.created_by  = :userid " +
            "       union all " +
            "       select p.folder_id from files p where p.folder_id in ( " +
            "       select f.folder_id  from folder_share f where f.user_id = :userid) " +
            "      ) a ", nativeQuery = true)
    List<Long> getFolderIdByuserid (@Param("userid") Long userid);

    @Query(value="select * from files f where upper(f.doc_type)= :doctype and f.status in (:status) and f.folder_id = :folderid", nativeQuery = true)
    Set<DocFile>  getDocFilebyStatusandDoctype(@Param("doctype") String doctype, @Param("status") List<String> status, @Param("folderid") Long folderid);


   /*@Query(value = "select id from files where folder_id = :folderId and status = :status and doc_type  not in ('NOT_DETECTED') order by id desc", nativeQuery = true)
    List<Long> getAllFileIdListByStatus(@Param("folderId") Long folderId, @Param("status") String status);
*/



    /*@Query (value="select * from files k where k.folder_id = :folderid and k.created_by in (:userid) ",nativeQuery = true)
    List<DocFile> getDocFilesFromFolderIdandUserId (@Param("folderid") Long folderid,@Param("userid") List<Long> userid);*/

    @Modifying
    @Transactional
    @Query(value="update files set created_by = :masteruserid  where folder_id = :folderid and created_by in ( :userid )", nativeQuery = true)
    void updateDocfilesByCreatedbyandFolderId(@Param("masteruserid") Long masteruserid ,@Param("folderid") List<Long>  folderid,@Param("userid") List<Integer> userid);

    @Query(value="select * from files f where upper(f.doc_type)= :doctype and f.status in (:status) and f.folder_id in (:folderid)", nativeQuery = true)
    Set<DocFile>  getDocFilebyStatusandDoctypeFolderId(@Param("doctype") String doctype, @Param("status") List<String> status, @Param("folderid") List<Long> folderid);


    @Query(value="select * from files f where f.folder_id in (:folderid)", nativeQuery = true)
    Set<DocFile>  getDocFilebyFolderId(@Param("folderid") List<Long> folderid);

    @Query(value= "select created_by from files where id = :fileid", nativeQuery = true)
    Long  getUserIdByFileId (@Param("fileid") Long fileid);

    @Query(value="select id from files where id in (:fileids) and doc_type not in ('others')", nativeQuery = true)
    List<Long> getFileIdByDocTypeExceptOthers(@Param("fileids") List<Long> fileids);


    @Transactional
    @Modifying
    DocFile save(DocFile docFile);


    @Query(value="select distinct g.id from (" +
            "select distinct p.id as id from files p where p.created_by = :userid" +
            "union all " +
            "select distinct k.id as id from files k where  k.folder_id in (  select distinct folder_id from folder_share where user_id = :userid) ) g", nativeQuery = true)
    List<Long> getAllFileIdsByUserId(@Param("userid") Long userid);


    @Transactional
    @Modifying
    @Query(value=" delete from files where id = :fileid", nativeQuery = true)
    void deleteFilesByIdDB(@Param("fileid") Long fileid);

    @Query(value="select ifnull(count(*), 0) from files where id = :fileid", nativeQuery = true)
    Long getCountFileByFileIDDB (@Param("fileid") Long fileid);

    @Query (value="select ifnull(length(location),0) from files where id = :fileid", nativeQuery = true)
    Long checkLocationOfFilesByFileid(@Param("fileid") Long fileid);

    @Query (value="select location from files where id = :fileid", nativeQuery = true)
    String getLocationOfFilesByFileid(@Param("fileid") Long fileid);

}
