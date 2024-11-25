package com.dev.aes.repository;

import com.dev.aes.entity.TransporterInfo;
import com.dev.aes.entity.TripInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransporterInfoRepository  extends JpaRepository<TransporterInfo, Long> {



    @Transactional
    @Modifying
    TransporterInfo save (TransporterInfo transporterInfo);


    @Query(value="select * from transporterinfo order by id desc" , nativeQuery = true)
    List<TransporterInfo> getAllTransporterInfo ();


    @Query(value = "select * from transporterinfo where id = :id", nativeQuery = true)
    TransporterInfo getTransporterInfoById (@Param("id") Long id);
}
