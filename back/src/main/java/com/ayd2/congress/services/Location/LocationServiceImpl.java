package com.ayd2.congress.services.Location;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ayd2.congress.dtos.Location.LocationResponse;
import com.ayd2.congress.dtos.Location.NewLocationRequest;
import com.ayd2.congress.dtos.Location.NewRoomRequest;
import com.ayd2.congress.dtos.Location.RoomResponse;
import com.ayd2.congress.dtos.Location.UpdateRoom;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.LocationMapper;
import com.ayd2.congress.models.Congress.ConferenceRoomEntity;
import com.ayd2.congress.models.Congress.LocationEntity;
import com.ayd2.congress.repositories.Congress.LocationRepository;
import com.ayd2.congress.repositories.Congress.RoomRepository;

@Service
public class LocationServiceImpl implements LocationService{
    private final LocationRepository locationRepository;
    private final RoomRepository roomRepository;
    private final LocationMapper locationMapper;

    public LocationServiceImpl(LocationRepository locationRepository, RoomRepository roomRepository,
            LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.roomRepository = roomRepository;
        this.locationMapper = locationMapper;
    }

    @Override
    public LocationResponse createLocation(NewLocationRequest request) {
        LocationEntity location = locationMapper.toEntity(request);
        locationRepository.save(location);
        return locationMapper.toResponse(location);
    }

    @Override
    public LocationEntity getLocationById(Long id) throws NotFoundException {
        return locationRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Location with id " + id + " not found")
        );
    }

    @Override
    public LocationResponse getLocationResponseById(Long id) throws NotFoundException {
        LocationEntity location = getLocationById(id);
        return locationMapper.toResponse(location);
    }

    @Override
    public RoomResponse createRoom(NewRoomRequest request,Long locationId) throws NotFoundException, DuplicatedEntityException {
        LocationEntity location = getLocationById(locationId);
        if (roomRepository.existsByNameAndLocationId(request.getName(), location.getId())) {
            throw new DuplicatedEntityException("Room with name " + request.getName() + " already exists in location " + location.getId());
        }
        ConferenceRoomEntity room = locationMapper.toRoomEntity(request);
        room.setLocation(location);
        roomRepository.save(room);
        return locationMapper.toRoomResponse(room);
    }

    @Override
    public RoomResponse getRoomResponseById(Long id) throws NotFoundException {
        ConferenceRoomEntity room = getRoomById(id);
        return locationMapper.toRoomResponse(room);
    }

    @Override
    public ConferenceRoomEntity getRoomById(Long id) throws NotFoundException {
        return roomRepository.findById(id).orElseThrow(
            () -> new NotFoundException("Room with id " + id + " not found")
        );
    }

    @Override
    public RoomResponse updateRoom(Long id, UpdateRoom request) throws NotFoundException, DuplicatedEntityException {
        ConferenceRoomEntity room = getRoomById(id);
        if (request.getName() != null && !request.getName().equals(room.getName())) {
            if (roomRepository.existsByNameAndLocationIdAndIdNot(request.getName(), room.getLocation().getId(), id)) {
                throw new DuplicatedEntityException("Room with name " + request.getName() + " already exists in location " + room.getLocation().getId());
            }
            room.setName(request.getName());
        }
        if (request.getCapacity() != null) {
            room.setCapacity(request.getCapacity());
        }
        roomRepository.save(room);
        return locationMapper.toRoomResponse(room);
    }

    @Override
    public List<RoomResponse> getRoomsByLocationId(Long locationId) throws NotFoundException {
        LocationEntity location = getLocationById(locationId);
        List<ConferenceRoomEntity> rooms = roomRepository.findByLocationId(location.getId());
        return locationMapper.toRoomResponseList(rooms);
    }

    @Override
    public List<LocationResponse> getAllLocations() {
        List<LocationEntity> locations = locationRepository.findAll();
        return locationMapper.toResponseList(locations);
    }
    
}
