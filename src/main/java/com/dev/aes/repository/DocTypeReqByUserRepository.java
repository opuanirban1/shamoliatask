package com.dev.aes.repository;

import com.dev.aes.entity.DocTypeReqByUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocTypeReqByUserRepository extends JpaRepository<DocTypeReqByUser, Long> {

    @Query(value="select * from doctypereqbyuser where id = :id", nativeQuery = true)
    DocTypeReqByUser getDocTypeReqByUserId(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(value="update doctypereqbyuser set status = :status where id=:doctypeid and requserid=:requserid", nativeQuery = true)
    public void updateDocTypeByIdandUserReq (@Param("doctypeid") Long doctypeid, @Param("status") String status, @Param("requserid") Long requserid);

    @Transactional
    @Modifying
    @Query(value="update doctypereqbyuser set doctypeid = :doctypeid where id=:id ", nativeQuery = true)
    public void updateDoctypeIdReqByuserByid(@Param("doctypeid") Long doctypeid, @Param("id") Long id);


}
