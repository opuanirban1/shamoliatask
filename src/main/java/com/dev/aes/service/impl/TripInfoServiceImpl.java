package com.dev.aes.service.impl;

import com.dev.aes.entity.TransporterInfo;
import com.dev.aes.entity.TripInfo;
import com.dev.aes.entity.User;
import com.dev.aes.payloads.request.AssignTransporterBookRequest;
import com.dev.aes.payloads.request.TripInfoInsertUpdateRequest;
import com.dev.aes.repository.TripInfoRepository;
import com.dev.aes.service.TransporterService;
import com.dev.aes.service.TripInfoService;
import com.dev.aes.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripInfoServiceImpl implements TripInfoService {

    @Autowired
    UserService userService;

    @Autowired
    TripInfoRepository tripInfoRepository;

    @Autowired
    TransporterService transporterService;


    @Override
    public TripInfo  updateInsertTripInfo(TripInfoInsertUpdateRequest tripInfoInsertUpdateRequest){

        User user = userService.getCurrentuser();

        TripInfo tripInfonew = new TripInfo();
        TripInfo tripInfo = new TripInfo();

        if (tripInfoInsertUpdateRequest.getId() == 0) {
            tripInfo =  tripInfoRepository.save(TripInfo.builder()
                    .pickuplat(tripInfoInsertUpdateRequest.getPickuplat())
                    .pickuplong(tripInfoInsertUpdateRequest.getPickuplong())
                    .pickuplocation(tripInfoInsertUpdateRequest.getPickuplocation())
                    .droplat(tripInfoInsertUpdateRequest.getDroplat())
                    .droplong(tripInfoInsertUpdateRequest.getDroplong())
                    .droplocation(tripInfoInsertUpdateRequest.getDroplocation())
                    .status(tripInfoInsertUpdateRequest.getStatus())
                    .user(user)
                    .createdAt(tripInfonew.getCreatedAt())
                    .customername(tripInfoInsertUpdateRequest.getCustomername())
                    .build());
        }else{

            tripInfo = tripInfoRepository.save(TripInfo.builder()
                    .id(tripInfoInsertUpdateRequest.getId())
                    .pickuplat(tripInfoInsertUpdateRequest.getPickuplat())
                    .pickuplong(tripInfoInsertUpdateRequest.getPickuplong())
                    .pickuplocation(tripInfoInsertUpdateRequest.getPickuplocation())
                    .droplat(tripInfoInsertUpdateRequest.getDroplat())
                    .droplong(tripInfoInsertUpdateRequest.getDroplong())
                    .droplocation(tripInfoInsertUpdateRequest.getDroplocation())
                    .status(tripInfoInsertUpdateRequest.getStatus())
                    .user(user)
                    .updatedAt(tripInfonew.getUpdatedAt())
                    .customername(tripInfoInsertUpdateRequest.getCustomername())
                    .build());

        }



        return tripInfo;
    }

    @Override
    public List<TripInfo> getAllTripInfoDB (){

        return tripInfoRepository.getALlTripInfo();
    }

    @Override
    public TripInfo  assignTransporterBookTrip(AssignTransporterBookRequest assignTransporterBookRequest){

        TripInfo tripInfo = new TripInfo();

        //TransporterInfo transporterInfodata = transporterService.getTransporterInfoById(assignTransporterBookRequest.getTransporterid());

      /*  tripInfo = tripInfoRepository.save(TripInfo.builder()
                        .id(assignTransporterBookRequest.getTripid())
                        .transporterInfo( transporterInfodata)
                        .updatedAt(tripInfo.getUpdatedAt())
                        .transporteraddtime(tripInfo.getTransporteraddtime())
                        .status(assignTransporterBookRequest.getStatus())
                .build());*/

        tripInfoRepository.updateTrifinoByAssignInfo(assignTransporterBookRequest.getTripid(), assignTransporterBookRequest.getStatus(), assignTransporterBookRequest.getTransporterid());

        tripInfo = tripInfoRepository.getTripInfoByidDB(assignTransporterBookRequest.getTripid());
        return tripInfo;
    }
}
