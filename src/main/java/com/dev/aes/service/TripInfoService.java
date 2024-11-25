package com.dev.aes.service;

import com.dev.aes.entity.TripInfo;
import com.dev.aes.payloads.request.AssignTransporterBookRequest;
import com.dev.aes.payloads.request.TripInfoInsertUpdateRequest;

import java.util.List;

public interface TripInfoService {

    public TripInfo updateInsertTripInfo(TripInfoInsertUpdateRequest tripInfoInsertUpdateRequest);

    public List<TripInfo> getAllTripInfoDB ();

    public TripInfo  assignTransporterBookTrip(AssignTransporterBookRequest assignTransporterBookRequest);
}
