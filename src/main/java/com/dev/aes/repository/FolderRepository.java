package com.dev.aes.repository;

import com.dev.aes.constant.FolderType;
import com.dev.aes.entity.Folder;
import com.dev.aes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    boolean existsByUserAndType(User currentuser, FolderType folderType);

    Set<Folder> findByCreatedBy(User user);

    Optional<Folder> findByUserAndType(User current, FolderType folderType);

    Set<Folder> findByParent(Folder folder);

  /*  @Query(value="select * from folders where id =?1 order by id asc",nativeQuery = true)
    Optional<Folder>  getAllFolderByIdOrderByIdAsc(Long folderid);

    @Query(value="select * from folders where parent_id =?1 order by id asc",nativeQuery = true)
    Set<Folder> findByParentOrderByIdAsc(Long folderparentid);*/

    Integer countByParentId(Long id);

    @Query(value = "SELECT f.id FROM folders f WHERE f.name = :name", nativeQuery = true)
    Long findIdByUsername(@Param("name") String name);

    boolean existsByParentIdAndName(Long parentId, String name);

    @Query(value = "SELECT * FROM folders f WHERE f.name LIKE %:name% AND f.created_byy IN :userIds", nativeQuery = true)
    List<Folder> search(@Param("name") String name, @Param("userIds") List<Long> userIds);

   /* @Query(value = "SELECT * FROM folders f WHERE f.name LIKE %:name% AND f.parent_id =:folderId", nativeQuery = true)
    Set<Folder> searchByParentFolderId(@Param("name") String name, @Param("folderId") Long folderId);*/

    @Query(value = "SELECT * FROM folders f WHERE f.name LIKE %:name% AND f.parent_id =:folderId", nativeQuery = true)
    Set<Folder> searchByParentFolderId(@Param("name") String name, @Param("folderId") Long folderId);

    @Query(value="select count(*) from folders  where parent_id =  :parentid", nativeQuery = true)
    Integer  searchByParentCount( Long parentid);


    @Query(value="   SELECT k.id, k.created_at, k.active, k.type, k.name," +
            "            k.parent_id, k.updated_at, " +
            "            k.user_id, " +
            "            k.created_byy " +
            "             FROM (SELECT p.id, " +
            "            p.created_at, " +
            "            p.active, " +
            "            p.type, " +
            "            p.name, " +
            "            p.parent_id, " +
            "            p.updated_at, " +
            "            p.user_id, " +
            "            p.created_byy, " +
            "            Row_Number() OVER (ORDER BY p.id DESC) MyRow FROM folders p where  p.parent_id =  :parentid) k " +
            "             WHERE MyRow BETWEEN :startlimit AND :endlimit ", nativeQuery = true)
    Set<Folder>  searchByParentWithPagination(@Param("startlimit") Integer startlimit,@Param("endlimit") Integer endlimit, @Param("parentid") Long parentid);

    @Query(value="select * from folders k where k.id = :fid", nativeQuery = true)
    Folder getFolderById(@Param("fid") Long fid);

    @Query(value="select ifnull(parent_id, 0) from folders k where k.id = :fid", nativeQuery = true)
    Long  getFolderParentIdById(@Param("fid") Long fid);

    /*       select distinct a.folder_id from (
       select k.folder_id  from dms4.files k where k.created_by  = (select u.id from dms4.users u where u.email = 'user008@gmail.com')

    union all
    select p.folder_id from dms4.files p where p.folder_id in (

            select fs.folder_id  from dms4.folder_share fs where fs.user_id =
            (select u.id from dms4.users u where u.email = 'user008@gmail.com'))

            ) a ;



    select * from dms4.files where folder_id = 338

    select * from dms4.folders where id =338;

    select * from dms4.folders f where f.id=
    union all
    select * from dms4.folders k where k.id =338

     */


    @Query(value="   with recursive cte (id)  as ( " +
            "        select :folderid" +
            "        union all " +
            "         select     id " +
            "          from       folders " +
            "         where      parent_id =  :folderid" +
            "         union all  " +
            "         select     p.id " +
            "          from      folders p " +
            "         inner join cte " +
            "                 on p.parent_id = cte.id " +
            "        ) " +
            "        select distinct * from cte ", nativeQuery = true)
    List<Long> getSubFolderIdsByFolderId(@Param("folderid") Long folderid);


    @Query(value="select count(*) from  files where status = 'ERROR DETECTED' and folder_id in (:folderids)", nativeQuery = true)
    Integer getErrorCountByFolderIdandErrorStatus (@Param("folderids") List<Long> folderids);
}
