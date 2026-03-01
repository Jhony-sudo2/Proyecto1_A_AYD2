package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.ayd2.congress.services.Location.LocationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class LocationServiceImplTest {

    private static final Long LOCATION_ID = 10L;
    private static final Long ROOM_ID = 20L;

    private static final String LOCATION_NAME = "Test Location";
    private static final String ADDRESS = "Test Address";
    private static final String CITY = "Test City";
    private static final String COUNTRY = "Test Country";

    private static final String ROOM_NAME = "Room A";
    private static final String ROOM_NAME_2 = "Room B";
    private static final Integer CAPACITY = 50;
    private static final Integer CAPACITY_2 = 80;
    private static final String EQUIPMENT = "Projector, Whiteboard";
    private static final String EQUIPMENT_2 = "Projector";
    private static final String DESCRIPTION = "Main conference room";
    private static final String DESCRIPTION_2 = "Updated description";

    @Mock private LocationRepository locationRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private LocationMapper locationMapper;

    @InjectMocks private LocationServiceImpl service;


    @Test
    void createLocation_savesEntityAndReturnsResponse() {
        // Arrange
        NewLocationRequest request = new NewLocationRequest(LOCATION_NAME, ADDRESS, CITY, COUNTRY);

        LocationEntity mapped = buildLocation(null, LOCATION_NAME, ADDRESS, CITY, COUNTRY);

        // Service returns mapper.toResponse(location) where "location" is the SAME instance created by toEntity(request)
        LocationResponse expected = new LocationResponse(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);

        when(locationMapper.toEntity(request)).thenReturn(mapped);
        when(locationRepository.save(same(mapped))).thenAnswer(inv -> {
            mapped.setId(LOCATION_ID); 
            return mapped;
        });
        when(locationMapper.toResponse(same(mapped))).thenReturn(expected);

        // Act
        LocationResponse result = service.createLocation(request);

        // Assert
        assertSame(expected, result);

        InOrder inOrder = inOrder(locationMapper, locationRepository);
        inOrder.verify(locationMapper).toEntity(request);
        inOrder.verify(locationRepository).save(mapped);
        inOrder.verify(locationMapper).toResponse(mapped);

        verifyNoInteractions(roomRepository);
        verifyNoMoreInteractions(locationMapper, locationRepository);
    }

    

    @Test
    void getLocationById_whenExists_returnsEntity() throws NotFoundException {
        // Arrange
        LocationEntity entity = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);
        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.of(entity));

        // Act
        LocationEntity result = service.getLocationById(LOCATION_ID);

        // Assert
        assertSame(entity, result);
        verify(locationRepository).findById(LOCATION_ID);

        verifyNoInteractions(roomRepository, locationMapper);
        verifyNoMoreInteractions(locationRepository);
    }

    @Test
    void getLocationById_whenNotExists_throwsNotFound() {
        // Arrange
        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getLocationById(LOCATION_ID));
        assertTrue(ex.getMessage().contains("Location with id " + LOCATION_ID + " not found"));

        verify(locationRepository).findById(LOCATION_ID);
        verifyNoInteractions(roomRepository, locationMapper);
        verifyNoMoreInteractions(locationRepository);
    }

    // ----------------------------
    // getLocationResponseById
    // ----------------------------

    @Test
    void getLocationResponseById_whenExists_returnsMappedResponse() throws NotFoundException {
        // Arrange
        LocationEntity entity = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);
        LocationResponse expected = new LocationResponse(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);

        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.of(entity));
        when(locationMapper.toResponse(entity)).thenReturn(expected);

        // Act
        LocationResponse result = service.getLocationResponseById(LOCATION_ID);

        // Assert
        assertSame(expected, result);

        InOrder inOrder = inOrder(locationRepository, locationMapper);
        inOrder.verify(locationRepository).findById(LOCATION_ID);
        inOrder.verify(locationMapper).toResponse(entity);

        verifyNoInteractions(roomRepository);
        verifyNoMoreInteractions(locationRepository, locationMapper);
    }

    @Test
    void getLocationResponseById_whenNotExists_throwsNotFound() {
        // Arrange
        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> service.getLocationResponseById(LOCATION_ID));

        verify(locationRepository).findById(LOCATION_ID);
        verifyNoInteractions(roomRepository, locationMapper);
        verifyNoMoreInteractions(locationRepository);
    }

    // ----------------------------
    // createRoom
    // ----------------------------

    @Test
    void createRoom_whenNoDuplicate_savesRoomWithLocationAndReturnsResponse()
            throws NotFoundException, DuplicatedEntityException {
        // Arrange
        LocationEntity location = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);

        NewRoomRequest request = new NewRoomRequest(
                ROOM_NAME,
                CAPACITY,
                EQUIPMENT,
                DESCRIPTION
        );

        ConferenceRoomEntity mappedRoom = buildRoom(null, ROOM_NAME, CAPACITY, EQUIPMENT, DESCRIPTION, null);

        RoomResponse expected = new RoomResponse(
                ROOM_ID,
                ROOM_NAME,
                CAPACITY,
                EQUIPMENT,
                DESCRIPTION
        );

        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.of(location));
        when(roomRepository.existsByNameAndLocationId(ROOM_NAME, LOCATION_ID)).thenReturn(false);
        when(locationMapper.toRoomEntity(request)).thenReturn(mappedRoom);

        when(roomRepository.save(same(mappedRoom))).thenAnswer(inv -> {
            mappedRoom.setId(ROOM_ID); // simulate id assignment
            return mappedRoom;
        });

        when(locationMapper.toRoomResponse(same(mappedRoom))).thenReturn(expected);

        // Act
        RoomResponse result = service.createRoom(request,LOCATION_ID);

        // Assert
        assertSame(expected, result);
        assertSame(location, mappedRoom.getLocation());

        InOrder inOrder = inOrder(locationRepository, roomRepository, locationMapper);
        inOrder.verify(locationRepository).findById(LOCATION_ID);
        inOrder.verify(roomRepository).existsByNameAndLocationId(ROOM_NAME, LOCATION_ID);
        inOrder.verify(locationMapper).toRoomEntity(request);
        inOrder.verify(roomRepository).save(mappedRoom);
        inOrder.verify(locationMapper).toRoomResponse(mappedRoom);

        verifyNoMoreInteractions(locationRepository, roomRepository, locationMapper);
    }

    @Test
    void createRoom_whenDuplicate_throwsDuplicatedEntityException() throws NotFoundException {
        // Arrange
        LocationEntity location = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);

        NewRoomRequest request = new NewRoomRequest(
                ROOM_NAME,
                CAPACITY,
                EQUIPMENT,
                DESCRIPTION
        );

        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.of(location));
        when(roomRepository.existsByNameAndLocationId(ROOM_NAME, LOCATION_ID)).thenReturn(true);

        // Act + Assert
        DuplicatedEntityException ex = assertThrows(DuplicatedEntityException.class, () -> service.createRoom(request,LOCATION_ID));
        assertTrue(ex.getMessage().contains("Room with name " + ROOM_NAME + " already exists"));

        verify(locationRepository).findById(LOCATION_ID);
        verify(roomRepository).existsByNameAndLocationId(ROOM_NAME, LOCATION_ID);

        verifyNoInteractions(locationMapper);
        verify(roomRepository, never()).save(any());

        verifyNoMoreInteractions(locationRepository, roomRepository);
    }

    @Test
    void createRoom_whenLocationNotFound_throwsNotFound() {
        // Arrange
        NewRoomRequest request = new NewRoomRequest(
                ROOM_NAME,
                CAPACITY,
                EQUIPMENT,
                DESCRIPTION
        );
        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> service.createRoom(request,LOCATION_ID));

        verify(locationRepository).findById(LOCATION_ID);
        verifyNoInteractions(roomRepository, locationMapper);
        verifyNoMoreInteractions(locationRepository);
    }

    // ----------------------------
    // getRoomById / getRoomResponseById
    // ----------------------------

    @Test
    void getRoomById_whenExists_returnsEntity() throws NotFoundException {
        // Arrange
        LocationEntity location = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);
        ConferenceRoomEntity room = buildRoom(ROOM_ID, ROOM_NAME, CAPACITY, EQUIPMENT, DESCRIPTION, location);

        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        // Act
        ConferenceRoomEntity result = service.getRoomById(ROOM_ID);

        // Assert
        assertSame(room, result);

        verify(roomRepository).findById(ROOM_ID);
        verifyNoInteractions(locationRepository, locationMapper);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    void getRoomById_whenNotExists_throwsNotFound() {
        // Arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // Act + Assert
        NotFoundException ex = assertThrows(NotFoundException.class, () -> service.getRoomById(ROOM_ID));
        assertTrue(ex.getMessage().contains("Room with id " + ROOM_ID + " not found"));

        verify(roomRepository).findById(ROOM_ID);
        verifyNoInteractions(locationRepository, locationMapper);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    void getRoomResponseById_whenExists_returnsMappedResponse() throws NotFoundException {
        // Arrange
        LocationEntity location = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);
        ConferenceRoomEntity room = buildRoom(ROOM_ID, ROOM_NAME, CAPACITY, EQUIPMENT, DESCRIPTION, location);

        RoomResponse expected = new RoomResponse(ROOM_ID, ROOM_NAME, CAPACITY, EQUIPMENT, DESCRIPTION);

        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(locationMapper.toRoomResponse(room)).thenReturn(expected);

        // Act
        RoomResponse result = service.getRoomResponseById(ROOM_ID);

        // Assert
        assertSame(expected, result);

        InOrder inOrder = inOrder(roomRepository, locationMapper);
        inOrder.verify(roomRepository).findById(ROOM_ID);
        inOrder.verify(locationMapper).toRoomResponse(room);

        verifyNoInteractions(locationRepository);
        verifyNoMoreInteractions(roomRepository, locationMapper);
    }

    @Test
    void getRoomResponseById_whenNotExists_throwsNotFound() {
        // Arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> service.getRoomResponseById(ROOM_ID));

        verify(roomRepository).findById(ROOM_ID);
        verifyNoInteractions(locationRepository, locationMapper);
        verifyNoMoreInteractions(roomRepository);
    }


    @Test
    void updateRoom_whenNameChangesAndCapacityProvided_updatesAndReturnsResponse()
            throws NotFoundException, DuplicatedEntityException {
        // Arrange
        LocationEntity location = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);
        ConferenceRoomEntity room = buildRoom(ROOM_ID, ROOM_NAME, CAPACITY, EQUIPMENT, DESCRIPTION, location);

        UpdateRoom request = new UpdateRoom(
                CAPACITY_2,
                DESCRIPTION_2,
                ROOM_NAME_2,
                EQUIPMENT_2
        );

        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNameAndLocationIdAndIdNot(ROOM_NAME_2, LOCATION_ID, ROOM_ID)).thenReturn(false);

        RoomResponse expected = new RoomResponse(ROOM_ID, ROOM_NAME_2, CAPACITY_2, EQUIPMENT_2, DESCRIPTION_2);
        when(locationMapper.toRoomResponse(room)).thenReturn(expected);

        // Act
        RoomResponse result = service.updateRoom(ROOM_ID, request);

        // Assert
        assertSame(expected, result);
        assertAll(
                () -> assertEquals(ROOM_NAME_2, result.getName()),
                () -> assertEquals(CAPACITY_2, result.getCapacity()),
                () -> assertEquals(EQUIPMENT_2, result.getEquipment()),
                () -> assertEquals(DESCRIPTION_2, result.getDescription())
        );

        InOrder inOrder = inOrder(roomRepository, locationMapper);
        inOrder.verify(roomRepository).findById(ROOM_ID);
        inOrder.verify(roomRepository).existsByNameAndLocationIdAndIdNot(ROOM_NAME_2, LOCATION_ID, ROOM_ID);
        inOrder.verify(roomRepository).save(room);
        inOrder.verify(locationMapper).toRoomResponse(room);

        verifyNoInteractions(locationRepository);
        verifyNoMoreInteractions(roomRepository, locationMapper);
    }

    @Test
    void updateRoom_whenNameChangesButDuplicate_throwsDuplicatedEntityException() throws NotFoundException {
        // Arrange
        LocationEntity location = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);
        ConferenceRoomEntity room = buildRoom(ROOM_ID, ROOM_NAME, CAPACITY, EQUIPMENT, DESCRIPTION, location);

        UpdateRoom request = new UpdateRoom(
                null,
                null,
                ROOM_NAME_2,
                null
        );

        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.existsByNameAndLocationIdAndIdNot(ROOM_NAME_2, LOCATION_ID, ROOM_ID)).thenReturn(true);

        // Act + Assert
        assertThrows(DuplicatedEntityException.class, () -> service.updateRoom(ROOM_ID, request));

        verify(roomRepository).findById(ROOM_ID);
        verify(roomRepository).existsByNameAndLocationIdAndIdNot(ROOM_NAME_2, LOCATION_ID, ROOM_ID);

        verify(roomRepository, never()).save(any());
        verifyNoInteractions(locationMapper, locationRepository);
        verifyNoMoreInteractions(roomRepository);
    }

    @Test
    void updateRoom_whenOnlyCapacityProvided_updatesAndReturnsResponse()
            throws NotFoundException, DuplicatedEntityException {
        // Arrange
        LocationEntity location = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);
        ConferenceRoomEntity room = buildRoom(ROOM_ID, ROOM_NAME, CAPACITY, EQUIPMENT, DESCRIPTION, location);

        UpdateRoom request = new UpdateRoom(
                CAPACITY_2,
                null,
                null,
                null
        );

        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));

        RoomResponse expected = new RoomResponse(ROOM_ID, ROOM_NAME, CAPACITY_2, EQUIPMENT, DESCRIPTION);
        when(locationMapper.toRoomResponse(room)).thenReturn(expected);

        // Act
        RoomResponse result = service.updateRoom(ROOM_ID, request);

        // Assert
        assertSame(expected, result);
        assertAll(
                () -> assertEquals(ROOM_NAME, room.getName()),
                () -> assertEquals(CAPACITY_2, room.getCapacity()),
                () -> assertEquals(EQUIPMENT, room.getEquipment()),
                () -> assertEquals(DESCRIPTION, room.getDescription())
        );

        verify(roomRepository).findById(ROOM_ID);
        verify(roomRepository).save(room);
        verify(locationMapper).toRoomResponse(room);

        verify(roomRepository, never()).existsByNameAndLocationIdAndIdNot(anyString(), anyLong(), anyLong());

        verifyNoInteractions(locationRepository);
        verifyNoMoreInteractions(roomRepository, locationMapper);
    }

    @Test
    void updateRoom_whenRoomNotFound_throwsNotFound() {
        // Arrange
        UpdateRoom request = new UpdateRoom(CAPACITY_2, DESCRIPTION_2, ROOM_NAME_2, EQUIPMENT_2);
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> service.updateRoom(ROOM_ID, request));

        verify(roomRepository).findById(ROOM_ID);
        verifyNoInteractions(locationRepository, locationMapper);
        verifyNoMoreInteractions(roomRepository);
    }

    
    @Test
    void getRoomsByLocationId_whenLocationExists_returnsMappedRoomList() throws NotFoundException {
        // Arrange
        LocationEntity location = buildLocation(LOCATION_ID, LOCATION_NAME, ADDRESS, CITY, COUNTRY);

        ConferenceRoomEntity r1 = buildRoom(1L, "R1", 10, "E1", "D1", location);
        ConferenceRoomEntity r2 = buildRoom(2L, "R2", 20, "E2", "D2", location);
        List<ConferenceRoomEntity> rooms = List.of(r1, r2);

        RoomResponse rr1 = new RoomResponse(1L, "R1", 10, "E1", "D1");
        RoomResponse rr2 = new RoomResponse(2L, "R2", 20, "E2", "D2");
        List<RoomResponse> expected = List.of(rr1, rr2);

        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.of(location));
        when(roomRepository.findByLocationId(LOCATION_ID)).thenReturn(rooms);
        when(locationMapper.toRoomResponseList(rooms)).thenReturn(expected);

        // Act
        List<RoomResponse> result = service.getRoomsByLocationId(LOCATION_ID);

        // Assert
        assertSame(expected, result);

        InOrder inOrder = inOrder(locationRepository, roomRepository, locationMapper);
        inOrder.verify(locationRepository).findById(LOCATION_ID);
        inOrder.verify(roomRepository).findByLocationId(LOCATION_ID);
        inOrder.verify(locationMapper).toRoomResponseList(rooms);

        verifyNoMoreInteractions(locationRepository, roomRepository, locationMapper);
    }

    @Test
    void getRoomsByLocationId_whenLocationNotFound_throwsNotFound() {
        // Arrange
        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(NotFoundException.class, () -> service.getRoomsByLocationId(LOCATION_ID));

        verify(locationRepository).findById(LOCATION_ID);
        verifyNoInteractions(roomRepository, locationMapper);
        verifyNoMoreInteractions(locationRepository);
    }

    private static LocationEntity buildLocation(Long id, String name, String address, String city, String country) {
        LocationEntity e = new LocationEntity();
        e.setId(id);
        e.setName(name);
        e.setAddress(address);
        e.setCity(city);
        e.setCountry(country);
        return e;
    }

    private static ConferenceRoomEntity buildRoom(Long id, String name, Integer capacity,
                                                  String equipment, String description,
                                                  LocationEntity location) {
        ConferenceRoomEntity r = new ConferenceRoomEntity();
        r.setId(id);
        r.setName(name);
        r.setCapacity(capacity);
        r.setEquipment(equipment);
        r.setDescription(description);
        r.setLocation(location);
        return r;
    }
}
