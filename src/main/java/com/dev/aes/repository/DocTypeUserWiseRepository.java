package com.dev.aes.repository;

import com.dev.aes.entity.DocTypeUserWise;
import jakarta.transaction.Transactional;
import org.bouncycastle.math.raw.Mod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocTypeUserWiseRepository extends JpaRepository <DocTypeUserWise, Long> {


    @Query(value="select * from doctypeuserwise where createdby = :userid", nativeQuery = true)
    List<DocTypeUserWise> getDocTypeUserWiseById(@Param("userid") Long Userid);


    @Query(value=" select ifnull(count(*),0) from doctypeuserwise where createdby=:userreqid and name = :docname", nativeQuery = true)
    Integer checkAlreadyExistDoctype(@Param("docname") String docname, @Param("userreqid") Long userreqid);


    @Transactional
    @Modifying
    DocTypeUserWise save(DocTypeUserWise docTypeUserWise);


    @Query(value="select name from doctypeuserwise where createdby = :userid", nativeQuery = true)
    List<String> getApprovedDoctypenamesByUserId (@Param("userid") Long userid);
}
