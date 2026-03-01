package com.ayd2.congress.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayd2.congress.dtos.Location.LocationResponse;
import com.ayd2.congress.dtos.Location.NewLocationRequest;
import com.ayd2.congress.dtos.Location.NewRoomRequest;
import com.ayd2.congress.dtos.Location.RoomResponse;
import com.ayd2.congress.dtos.Location.UpdateRoom;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.services.Location.LocationService;

@RestController
@RequestMapping("/locations")
public class LocationController {
    private final LocationService locationService;
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@Validated @RequestBody NewLocationRequest request){
        LocationResponse response = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/rooms")
    public ResponseEntity<RoomResponse> createRoom(@PathVariable Long id,@Validated @RequestBody NewRoomRequest request) throws NotFoundException, DuplicatedEntityException{
        RoomResponse response = locationService.createRoom(request, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{locationId}/rooms")
    public ResponseEntity<List<RoomResponse>> getRoomsByLocationId(@PathVariable Long locationId) throws NotFoundException {
        return ResponseEntity.ok(locationService.getRoomsByLocationId(locationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long id) throws NotFoundException {
        LocationResponse response = locationService.getLocationResponseById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) throws NotFoundException {
        RoomResponse response = locationService.getRoomResponseById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/rooms/{id}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable Long id,@Validated @RequestBody UpdateRoom request) throws NotFoundException, DuplicatedEntityException {
        RoomResponse response = locationService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }

}
