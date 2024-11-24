package com.dev.aes.repository;

import com.dev.aes.entity.LocationTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationTrackRepository extends JpaRepository<LocationTrack, Long> {
}
