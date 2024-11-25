package com.dev.aes.repository;

import com.dev.aes.entity.TripInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripInfoRepository extends JpaRepository<TripInfo, Long>  {

    @Transactional
    @Modifying
    TripInfo save (TripInfo tripInfo);


    @Query(value="select * from tripinfo order by id desc" , nativeQuery = true)
    List<TripInfo> getALlTripInfo ();


    @Query(value="select * from tripinfo where id = :tripid" , nativeQuery = true)
    TripInfo getTripInfoByidDB (@Param("tripid") Long tripid);

    @Transactional
    @Modifying
    @Query(value="update tripinfo set transporter_id = :transporterid, status = :status , transporteraddtime = now(), " +
            " updateadat = now() where id =:tripid", nativeQuery = true)
    void updateTrifinoByAssignInfo(@Param("tripid") Long tripid, @Param("status") String status, @Param("transporterid") Long transporterid);
}
