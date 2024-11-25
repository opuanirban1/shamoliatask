package com.dev.aes.repository;

import com.dev.aes.entity.LocationTrack;
import com.dev.aes.entity.TransporterInfo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationTrackRepository extends JpaRepository<LocationTrack, Long> {

    @Transactional
    @Modifying
    LocationTrack save (LocationTrack locationTrack);

}
