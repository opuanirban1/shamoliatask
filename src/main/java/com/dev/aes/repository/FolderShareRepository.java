package com.dev.aes.repository;

import com.dev.aes.entity.Folder;
import com.dev.aes.entity.FolderShare;
import jakarta.transaction.Transactional;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderShareRepository extends JpaRepository<FolderShare, Long> {

    List<FolderShare> findAllByUserId(Long id);

    @Query(value="select p.user_id from folder_share p where p.folder_id = ?1", nativeQuery = true)
    List<Integer>  findUserIdByFolderId (Long folderid);

    @Query(value="select * from folder_share where id = :foldershareid", nativeQuery = true)
    FolderShare getFoldershareById(@Param("foldershareid") Integer foldershareid);

    @Query(value="select * from folder_share p where p.folder_id =:folderId and p.user_id =:userId", nativeQuery = true)
    Optional<FolderShare> findSharedFolderByUserIdAndFolderId (@Param("folderId")Long folderId, @Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query(value="delete from folder_share p where p.folder_id = :folderid and p.user_id in (" +
            ":userid" +
            ")", nativeQuery = true)
    void doRevokeUserIdByfolderId(@Param("folderid") Integer folderid, @Param("userid") List<Integer> userid);

    List<FolderShare> findAllByFolderId(Long folderId);

    @Transactional
    @Modifying
    @Query(value="delete from folder_share s where s.folder_id= :folderId", nativeQuery = true)
    void deleteSharedFoldersUsingFolderId(Long folderId);

    @Query(value="select count(*) from folder_share  where user_id = :userid", nativeQuery = true)
    Integer  findAllByUserIDWithPaginationCount (@Param("userid") Long userid);


    @Query(value="select " +
            "k.id, " +
            "k.created_at, " +
            "k.folder_id, " +
            "k.updated_at, " +
            "k.user_id " +
            "from (  select p.id, " +
            "p.created_at, " +
            "p.folder_id, " +
            "p.updated_at, " +
            "p.user_id, " +
            " Row_Number() OVER (ORDER BY p.id DESC) MyRow FROM folder_share p where  p.user_id = :userid) k " +
            "                       WHERE MyRow BETWEEN :startlimit AND :endlimit ", nativeQuery = true)
    List<FolderShare>  findAllByUserIDWithPaginationData ( @Param("startlimit") Integer startlimit,@Param("endlimit") Integer endlimit,@Param("userid") Long userid);



}
