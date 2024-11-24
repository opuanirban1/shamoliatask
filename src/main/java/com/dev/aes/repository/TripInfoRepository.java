package com.dev.aes.repository;

import com.dev.aes.entity.SysSetting;
import com.dev.aes.entity.TripInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripInfoRepository extends JpaRepository<TripInfo, Long>  {
}
