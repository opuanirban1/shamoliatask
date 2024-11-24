package com.dev.aes.repository;

import com.dev.aes.entity.AllEncryption;
import com.dev.aes.entity.UserWiseEncryption;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserwiseencryptionRepository extends JpaRepository<UserWiseEncryption, Long> {

    //@Query(value="select * from userwiseencryption where id = (select max(id) from  userwiseencryption where userid = :userid and password = :password) and userid=:userid and password = :password", nativeQuery = true)
    @Query(value="select * from userwiseencryption where id = (select max(id) from  userwiseencryption where userid = :userid) and userid=:userid", nativeQuery = true)
    UserWiseEncryption getUserwiseEncrytionList(@Param("userid") Long userid/*, @Param("password") String password*/);


    @Query(value="select ifnull(count(*),0) from userwiseencryption where id = (select max(id) from  userwiseencryption where userid = :userid and password = :password) and userid=:userid and password = :password", nativeQuery = true)
        //@Query(value="select * from userwiseencryption where id = (select max(id) from  userwiseencryption where userid = :userid) and userid=:userid", nativeQuery = true)
    Integer  countUserwiseEncryptionList(@Param("userid") Long userid, @Param("password") String password);


    @Query(value="select ifnull(count(*),0) from userwiseencryption where id = (select max(id) from  userwiseencryption where userid = :userid) and userid=:userid", nativeQuery = true)
        //@Query(value="select * from userwiseencryption where id = (select max(id) from  userwiseencryption where userid = :userid) and userid=:userid", nativeQuery = true)
    Integer  countUserwiseEncryptionListWithUserid(@Param("userid") Long userid);



    @Transactional
    @Modifying
    @Query(value="update userwiseencryption set encryptionname = :encryptionname,  updateby=:userid, updated_at = now() where id=:id ", nativeQuery = true)
    void updateUserWiseEncryption(@Param("encryptionname") String encryptionname,@Param("id") Long id, @Param("userid") Long userid);

    @Transactional
    @Modifying
    @Query(value="delete from  userwiseencryption  where id=:id ", nativeQuery = true)
    void updateDisableDeleteUserWiseEncryption(@Param("id") Long id);


}
