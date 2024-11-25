package com.dev.aes.service.impl;

import com.dev.aes.entity.TransporterInfo;
import com.dev.aes.entity.TripInfo;
import com.dev.aes.entity.User;
import com.dev.aes.payloads.request.TransporterInserUpdateRequest;
import com.dev.aes.payloads.request.TripInfoInsertUpdateRequest;
import com.dev.aes.repository.TransporterInfoRepository;
import com.dev.aes.repository.TripInfoRepository;
import com.dev.aes.service.TransporterService;
import com.dev.aes.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransporterServiceImpl implements TransporterService {

    @Autowired
    UserService userService;

    @Autowired
    TransporterInfoRepository transporterInfoRepository;


    @Override
    public TransporterInfo updateInsertTransporterInfo(TransporterInserUpdateRequest transporterInserUpdateRequest){
        TransporterInfo transporterInfo = new TransporterInfo();

        if (transporterInserUpdateRequest.getId() == 0) {
            transporterInfo  =  transporterInfoRepository.save(TransporterInfo.builder()
                            .email(transporterInserUpdateRequest.getEmail())
                            .name(transporterInserUpdateRequest.getName())
                            .phoneno(transporterInserUpdateRequest.getPhoneno())
                            .address(transporterInserUpdateRequest.getAddress())
                            .createdAt( transporterInfo.getCreatedAt())
                            .build());
        }else{
            transporterInfo =  transporterInfoRepository.save(TransporterInfo.builder()
                            .id(transporterInfo.getId())
                    .email(transporterInserUpdateRequest.getEmail())
                    .name(transporterInserUpdateRequest.getName())
                    .phoneno(transporterInserUpdateRequest.getPhoneno())
                    .address(transporterInserUpdateRequest.getAddress())
                    .updatedAt(transporterInfo.getUpdatedAt())
                    .build());
        }
        return   transporterInfo;
    }

    @Override
    public List<TransporterInfo> getAllTripInfoDB (){
        return transporterInfoRepository.getAllTransporterInfo();
    }

    @Override
    public TransporterInfo getTransporterInfoById (Long id){
        return transporterInfoRepository.getTransporterInfoById(id);
    }
}
