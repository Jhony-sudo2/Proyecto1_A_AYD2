package com.ayd2.congress.services.Location;

import java.util.List;

import com.ayd2.congress.dtos.Location.LocationResponse;
import com.ayd2.congress.dtos.Location.NewLocationRequest;
import com.ayd2.congress.dtos.Location.NewRoomRequest;
import com.ayd2.congress.dtos.Location.RoomResponse;
import com.ayd2.congress.dtos.Location.UpdateRoom;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.exceptions.RoomHasActivitiesException;
import com.ayd2.congress.models.Congress.ConferenceRoomEntity;
import com.ayd2.congress.models.Congress.LocationEntity;

public interface LocationService {
    LocationResponse createLocation(NewLocationRequest request);
    LocationEntity getLocationById(Long id) throws NotFoundException;
    LocationResponse getLocationResponseById(Long id) throws NotFoundException;
    RoomResponse createRoom(NewRoomRequest request,Long locationId) throws NotFoundException, DuplicatedEntityException;
    RoomResponse getRoomResponseById(Long id) throws NotFoundException;
    ConferenceRoomEntity getRoomById(Long id) throws NotFoundException;
    RoomResponse updateRoom(Long id,UpdateRoom request) throws NotFoundException, DuplicatedEntityException;
    List<RoomResponse> getRoomsByLocationId(Long locationId) throws NotFoundException;
    List<LocationResponse> getAllLocations();
    void deleteRoom(Long id) throws NotFoundException,RoomHasActivitiesException;
}
