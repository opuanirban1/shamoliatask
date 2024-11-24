package com.dev.aes.repository;

import com.dev.aes.entity.SysSetting;
import com.dev.aes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SysSettingRepository extends JpaRepository<SysSetting, Long> {

    @Query(value="select * from sys_setting order by id", nativeQuery = true)
    List<SysSetting> getAllSystemSetting();
}
