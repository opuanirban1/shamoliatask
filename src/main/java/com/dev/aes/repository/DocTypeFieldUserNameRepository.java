package com.dev.aes.repository;

import com.dev.aes.entity.DocTypeField;
import com.dev.aes.entity.DocTypeFieldusername;
import com.dev.aes.entity.DocTypeObject;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocTypeFieldUserNameRepository extends JpaRepository<DocTypeFieldusername, Long> {


    @Transactional
    @Query(value=" select a.language from doctypefieldusername a where a.id =  " +
            " (select max(b.id) from doctypefieldusername b where b.doctype_id= :doctypeid and b.username = :username " +
            " and b.doctypefield_id  =:doctypefield_id) " +
            " and a.doctype_id = :doctypeid and a.username= :username and a.doctypefield_id =:doctypefield_id ", nativeQuery = true)
    String getLanguageByDoctypeandUsername(@Param("doctypeid") Integer doctypeid, @Param("username") String username
                                       , @Param("doctypefield_id") Integer doctypefield_id);

}

