package com.dev.aes.service.impl;

import com.dev.aes.entity.LocationTrack;
import com.dev.aes.entity.TransporterInfo;
import com.dev.aes.entity.User;
import com.dev.aes.payloads.request.LocationTrackRequest;
import com.dev.aes.payloads.request.TransporterInserUpdateRequest;
import com.dev.aes.repository.LocationTrackRepository;
import com.dev.aes.service.LocationTrackService;
import com.dev.aes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.stereotype.Service;

@Service
public class LocationTrackServiceImpl implements LocationTrackService {

    @Autowired
    UserService userService;

    @Autowired
    LocationTrackRepository locationTrackRepository;

    @Override
    public LocationTrack updateInsertLocationTrack(LocationTrackRequest locationTrackRequest) {

        User user = userService.getCurrentuser();

        LocationTrack locationTracknew = new LocationTrack();
        LocationTrack locationTrack = new LocationTrack();

        if (locationTrackRequest.getId() == 0) {
            locationTrack  =  locationTrackRepository.save(LocationTrack.builder()
                            .locationlat(locationTrackRequest.getLocationlat())
                            .locationlong(locationTrackRequest.getLocationlong())
                    .createdAt( locationTracknew.getCreatedAt())
                    .build());
        }else{

            locationTrack =
                    locationTrackRepository.save(LocationTrack.builder()
                                    .id(locationTrackRequest.getId())
                            .locationlat(locationTrackRequest.getLocationlat())
                            .locationlong(locationTrackRequest.getLocationlong())
                            .createdAt( locationTracknew.getCreatedAt())
                            .build());

        }



        return   locationTrack;
    }

}
