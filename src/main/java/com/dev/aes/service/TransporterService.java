package com.dev.aes.service;

import com.dev.aes.entity.TransporterInfo;
import com.dev.aes.payloads.request.TransporterInserUpdateRequest;

import java.util.List;

public interface TransporterService {
    public TransporterInfo updateInsertTransporterInfo(TransporterInserUpdateRequest transporterInserUpdateRequest);
    public List<TransporterInfo> getAllTripInfoDB ();
    public TransporterInfo getTransporterInfoById (Long id);
}
