package com.dev.aes.repository;

import com.dev.aes.entity.User;
import com.dev.aes.entity.UserWiseSysSetting;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserWiseSysSettingRepository extends JpaRepository<UserWiseSysSetting, Long> {

    @Query(value = "select ifnull(usersyssettingid,0) from userwise_sys_setting where userid = :userid order by id desc limit 1" , nativeQuery = true)
    Long getLatestUserSystemSettinInput (@Param("userid") Long userid);


    @Transactional
    @Modifying
    @Query(value="insert into userwise_sys_setting (userid, usersyssettingid, created_at, status) values (:userid, :usersyssettingid, now(), :status)", nativeQuery = true)
    void insertUserWiseSysSetting(@Param("userid") Long userid, @Param("usersyssettingid") Integer usersyssettingid , @Param("status") String status);



}
