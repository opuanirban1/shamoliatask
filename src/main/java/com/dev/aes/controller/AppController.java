package com.dev.aes.controller;

import com.dev.aes.entity.DocFile;
import com.dev.aes.payloads.request.AssignTransporterBookRequest;
import com.dev.aes.payloads.request.LocationTrackRequest;
import com.dev.aes.payloads.request.TransporterInserUpdateRequest;
import com.dev.aes.payloads.request.TripInfoInsertUpdateRequest;
import com.dev.aes.service.LocationTrackService;
import com.dev.aes.service.TransporterService;
import com.dev.aes.service.TripInfoService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/api/v1")
public class AppController {

    private final Logger LOG = LoggerFactory.getLogger(APIController.class);

    @Autowired
    TripInfoService tripInfoService;

    @Autowired
    LocationTrackService locationTrackService;

    @Autowired
    TransporterService transporterService;

    @PostMapping("/insertUpdateTripInfo")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> insertupdatetripinfo (@Valid @RequestBody TripInfoInsertUpdateRequest tripInfoInsertUpdateRequest) throws Exception {
        LOG.info("API endpoint /api/v1/insertUpdateTripInfo and input {}", tripInfoInsertUpdateRequest);
        return new ResponseEntity<>(tripInfoService.updateInsertTripInfo(tripInfoInsertUpdateRequest), HttpStatus.OK);
    }


    @GetMapping("/getAllTripInfo")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getAllTripInfo () throws Exception {
        LOG.info("API endpoint /api/v1/getAllTripInfo");
        return new ResponseEntity<>(tripInfoService.getAllTripInfoDB(), HttpStatus.OK);
    }

    @GetMapping("/getAllTransporterInfo")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> getAllTransporterInfo () throws Exception {
        LOG.info("API endpoint /api/v1/getAllTransporterInfo");
        return new ResponseEntity<>( transporterService.getAllTripInfoDB(), HttpStatus.OK);
    }


    @PostMapping("/insertUpdateLocationTrack")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> insertUpdateLocationTrack (@Valid @RequestBody LocationTrackRequest locationTrackRequest) throws Exception {
        LOG.info("API endpoint /api/v1/insertUpdateLocationTrack and input {}", locationTrackRequest);
        return new ResponseEntity<>(locationTrackService.updateInsertLocationTrack(locationTrackRequest), HttpStatus.OK);
    }


    @PostMapping("/insertUpdateTransporterInfo")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> insertUpdateTransporterInfo (@Valid @RequestBody TransporterInserUpdateRequest transporterInserUpdateRequest) throws Exception {
        LOG.info("API endpoint /api/v1/insertUpdateTransporterInfo and input {}",transporterInserUpdateRequest);
        return new ResponseEntity<>(transporterService.updateInsertTransporterInfo(transporterInserUpdateRequest), HttpStatus.OK);
    }

    @PostMapping("/assignTransporterBook")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'ORG_ADMIN', 'USER')")
    public ResponseEntity<?> assignTransporterBook (@Valid @RequestBody AssignTransporterBookRequest assignTransporterBookRequest) throws Exception {
        LOG.info("API endpoint /api/v1/assignTransporterBook and input {}",assignTransporterBookRequest);
        return new ResponseEntity<>(tripInfoService.assignTransporterBookTrip(assignTransporterBookRequest), HttpStatus.OK);
    }


}
