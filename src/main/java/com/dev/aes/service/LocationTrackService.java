package com.dev.aes.service;

import com.dev.aes.entity.LocationTrack;
import com.dev.aes.payloads.request.LocationTrackRequest;

public interface LocationTrackService {

    public LocationTrack updateInsertLocationTrack(LocationTrackRequest locationTrackRequest);
}
