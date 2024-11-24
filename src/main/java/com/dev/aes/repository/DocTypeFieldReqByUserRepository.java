package com.dev.aes.repository;

import com.dev.aes.entity.Dictionary;
import com.dev.aes.entity.DocTypeFieldReqByUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocTypeFieldReqByUserRepository extends JpaRepository<DocTypeFieldReqByUser, Long> {



    @Transactional
    @Modifying
    @Query(value="update doctypefieldreqbyuser set status = :status where doctype_id=:doctypeid and requserid = :userid ", nativeQuery = true)
    public void updateDocTypeFiledByIdandUserReq (@Param("doctypeid") Long doctypeid, @Param("status")  String status, @Param("userid") Long userid );

}
