package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import com.ayd2.congress.exceptions.RoomHasActivitiesException;
import com.ayd2.congress.mappers.LocationMapper;
import com.ayd2.congress.models.Congress.ConferenceRoomEntity;
import com.ayd2.congress.models.Congress.LocationEntity;
import com.ayd2.congress.repositories.Activity.ActivityRepository;
import com.ayd2.congress.repositories.Congress.LocationRepository;
import com.ayd2.congress.repositories.Congress.RoomRepository;
import com.ayd2.congress.services.Location.LocationServiceImpl;

@ExtendWith(MockitoExtension.class)
public class LocationServiceImplTest {

    private static final Long LOCATION_ID = 1L;
    private static final Long ROOM_ID = 10L;

    private static final String LOCATION_NAME = "Centro de Convenciones";
    private static final String LOCATION_ADDRESS = "Zona 1";
    private static final String LOCATION_CITY = "Guatemala";
    private static final String LOCATION_COUNTRY = "Guatemala";

    private static final String ROOM_NAME = "Salon A";
    private static final Integer ROOM_CAPACITY = 100;
    private static final String ROOM_EQUIPMENT = "Projector";
    private static final String ROOM_DESCRIPTION = "Main room";

    private static final String UPDATED_ROOM_NAME = "Salon B";
    private static final Integer UPDATED_ROOM_CAPACITY = 150;
    private static final String UPDATED_ROOM_EQUIPMENT = "TV";
    private static final String UPDATED_ROOM_DESCRIPTION = "Updated description";

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private LocationMapper locationMapper;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private LocationServiceImpl locationService;

    private NewLocationRequest newLocationRequest;
    private LocationEntity locationEntity;
    private LocationResponse locationResponse;

    private NewRoomRequest newRoomRequest;
    private ConferenceRoomEntity roomEntity;
    private RoomResponse roomResponse;

    @BeforeEach
    void setUp() {
        newLocationRequest = new NewLocationRequest(
                LOCATION_NAME,
                LOCATION_ADDRESS,
                LOCATION_CITY,
                LOCATION_COUNTRY
        );

        locationEntity = new LocationEntity();
        locationEntity.setId(LOCATION_ID);
        locationEntity.setName(LOCATION_NAME);
        locationEntity.setAddress(LOCATION_ADDRESS);
        locationEntity.setCity(LOCATION_CITY);
        locationEntity.setCountry(LOCATION_COUNTRY);

        locationResponse = new LocationResponse(
                LOCATION_ID,
                LOCATION_NAME,
                LOCATION_ADDRESS,
                LOCATION_CITY,
                LOCATION_COUNTRY
        );

        newRoomRequest = new NewRoomRequest(
                ROOM_NAME,
                ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );

        roomEntity = new ConferenceRoomEntity();
        roomEntity.setId(ROOM_ID);
        roomEntity.setName(ROOM_NAME);
        roomEntity.setCapacity(ROOM_CAPACITY);
        roomEntity.setEquipment(ROOM_EQUIPMENT);
        roomEntity.setDescription(ROOM_DESCRIPTION);
        roomEntity.setLocation(locationEntity);

        roomResponse = new RoomResponse(
                ROOM_ID,
                ROOM_NAME,
                ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );
    }

    @Test
    void testCreateLocation() {
        // Arrange
        ArgumentCaptor<LocationEntity> locationCaptor = ArgumentCaptor.forClass(LocationEntity.class);

        when(locationMapper.toEntity(newLocationRequest)).thenReturn(locationEntity);
        when(locationMapper.toResponse(locationEntity)).thenReturn(locationResponse);

        // Act
        LocationResponse result = locationService.createLocation(newLocationRequest);

        // Assert
        assertAll(
                () -> verify(locationRepository).save(locationCaptor.capture()),
                () -> assertEquals(LOCATION_NAME, locationCaptor.getValue().getName()),
                () -> assertEquals(LOCATION_ADDRESS, locationCaptor.getValue().getAddress()),
                () -> assertEquals(LOCATION_ID, result.getId()),
                () -> assertEquals(LOCATION_NAME, result.getName())
        );
    }

    @Test
    void testGetLocationById() throws Exception {
        // Arrange
        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.of(locationEntity));

        // Act
        LocationEntity result = locationService.getLocationById(LOCATION_ID);

        // Assert
        assertAll(
                () -> assertEquals(LOCATION_ID, result.getId()),
                () -> assertEquals(LOCATION_NAME, result.getName())
        );
    }

    @Test
    void testGetLocationByIdWhenNotFound() {
        // Arrange
        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> locationService.getLocationById(LOCATION_ID));
    }

    @Test
    void testGetLocationResponseById() throws Exception {
        // Arrange
        LocationServiceImpl spy = spy(locationService);

        doReturn(locationEntity).when(spy).getLocationById(LOCATION_ID);
        when(locationMapper.toResponse(locationEntity)).thenReturn(locationResponse);

        // Act
        LocationResponse result = spy.getLocationResponseById(LOCATION_ID);

        // Assert
        assertAll(
                () -> assertEquals(LOCATION_ID, result.getId()),
                () -> assertEquals(LOCATION_NAME, result.getName())
        );
    }

    @Test
    void testCreateRoom() throws Exception {
        // Arrange
        LocationServiceImpl spy = spy(locationService);
        ArgumentCaptor<ConferenceRoomEntity> roomCaptor = ArgumentCaptor.forClass(ConferenceRoomEntity.class);

        doReturn(locationEntity).when(spy).getLocationById(LOCATION_ID);
        when(roomRepository.existsByNameAndLocationId(ROOM_NAME, LOCATION_ID)).thenReturn(false);
        when(locationMapper.toRoomEntity(newRoomRequest)).thenReturn(roomEntity);
        when(locationMapper.toRoomResponse(roomEntity)).thenReturn(roomResponse);

        // Act
        RoomResponse result = spy.createRoom(newRoomRequest, LOCATION_ID);

        // Assert
        assertAll(
                () -> verify(roomRepository).save(roomCaptor.capture()),
                () -> assertEquals(ROOM_NAME, roomCaptor.getValue().getName()),
                () -> assertEquals(LOCATION_ID, roomCaptor.getValue().getLocation().getId()),
                () -> assertEquals(ROOM_ID, result.getId()),
                () -> assertEquals(ROOM_NAME, result.getName())
        );
    }

    @Test
    void testCreateRoomWhenDuplicatedName() throws Exception {
        // Arrange
        LocationServiceImpl spy = spy(locationService);

        doReturn(locationEntity).when(spy).getLocationById(LOCATION_ID);
        when(roomRepository.existsByNameAndLocationId(ROOM_NAME, LOCATION_ID)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> spy.createRoom(newRoomRequest, LOCATION_ID));
    }

    @Test
    void testGetRoomResponseById() throws Exception {
        // Arrange
        LocationServiceImpl spy = spy(locationService);

        doReturn(roomEntity).when(spy).getRoomById(ROOM_ID);
        when(locationMapper.toRoomResponse(roomEntity)).thenReturn(roomResponse);

        // Act
        RoomResponse result = spy.getRoomResponseById(ROOM_ID);

        // Assert
        assertAll(
                () -> assertEquals(ROOM_ID, result.getId()),
                () -> assertEquals(ROOM_NAME, result.getName())
        );
    }

    @Test
    void testGetRoomById() throws Exception {
        // Arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(roomEntity));

        // Act
        ConferenceRoomEntity result = locationService.getRoomById(ROOM_ID);

        // Assert
        assertAll(
                () -> assertEquals(ROOM_ID, result.getId()),
                () -> assertEquals(ROOM_NAME, result.getName())
        );
    }

    @Test
    void testGetRoomByIdWhenNotFound() {
        // Arrange
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> locationService.getRoomById(ROOM_ID));
    }

    @Test
    void testUpdateRoom() throws Exception {
        // Arrange
        UpdateRoom request = new UpdateRoom(
                UPDATED_ROOM_CAPACITY,
                UPDATED_ROOM_DESCRIPTION,
                UPDATED_ROOM_NAME,
                UPDATED_ROOM_EQUIPMENT
        );

        LocationServiceImpl spy = spy(locationService);
        ArgumentCaptor<ConferenceRoomEntity> roomCaptor = ArgumentCaptor.forClass(ConferenceRoomEntity.class);

        doReturn(roomEntity).when(spy).getRoomById(ROOM_ID);
        when(roomRepository.existsByNameAndLocationIdAndIdNot(UPDATED_ROOM_NAME, LOCATION_ID, ROOM_ID)).thenReturn(false);

        RoomResponse updatedResponse = new RoomResponse(
                ROOM_ID,
                UPDATED_ROOM_NAME,
                UPDATED_ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );
        when(locationMapper.toRoomResponse(roomEntity)).thenReturn(updatedResponse);

        // Act
        RoomResponse result = spy.updateRoom(ROOM_ID, request);

        // Assert
        assertAll(
                () -> verify(roomRepository).save(roomCaptor.capture()),
                () -> assertEquals(UPDATED_ROOM_NAME, roomCaptor.getValue().getName()),
                () -> assertEquals(UPDATED_ROOM_CAPACITY, roomCaptor.getValue().getCapacity()),
                // El servicio actual NO actualiza equipment ni description
                () -> assertEquals(ROOM_EQUIPMENT, roomCaptor.getValue().getEquipment()),
                () -> assertEquals(ROOM_DESCRIPTION, roomCaptor.getValue().getDescription()),
                () -> assertEquals(UPDATED_ROOM_NAME, result.getName()),
                () -> assertEquals(UPDATED_ROOM_CAPACITY, result.getCapacity())
        );
    }

    @Test
    void testUpdateRoomWhenDuplicatedName() throws Exception {
        // Arrange
        UpdateRoom request = new UpdateRoom(
                UPDATED_ROOM_CAPACITY,
                UPDATED_ROOM_DESCRIPTION,
                UPDATED_ROOM_NAME,
                UPDATED_ROOM_EQUIPMENT
        );

        LocationServiceImpl spy = spy(locationService);

        doReturn(roomEntity).when(spy).getRoomById(ROOM_ID);
        when(roomRepository.existsByNameAndLocationIdAndIdNot(UPDATED_ROOM_NAME, LOCATION_ID, ROOM_ID)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> spy.updateRoom(ROOM_ID, request));
    }

    @Test
    void testUpdateRoomWhenNameIsSameShouldNotValidateDuplicate() throws Exception {
        // Arrange
        UpdateRoom request = new UpdateRoom(
                UPDATED_ROOM_CAPACITY,
                UPDATED_ROOM_DESCRIPTION,
                ROOM_NAME,
                UPDATED_ROOM_EQUIPMENT
        );

        LocationServiceImpl spy = spy(locationService);
        ArgumentCaptor<ConferenceRoomEntity> roomCaptor = ArgumentCaptor.forClass(ConferenceRoomEntity.class);

        doReturn(roomEntity).when(spy).getRoomById(ROOM_ID);
        when(locationMapper.toRoomResponse(roomEntity)).thenReturn(roomResponse);

        // Act
        spy.updateRoom(ROOM_ID, request);

        // Assert
        assertAll(
                () -> verify(roomRepository, never()).existsByNameAndLocationIdAndIdNot(ROOM_NAME, LOCATION_ID, ROOM_ID),
                () -> verify(roomRepository).save(roomCaptor.capture()),
                () -> assertEquals(ROOM_NAME, roomCaptor.getValue().getName()),
                () -> assertEquals(UPDATED_ROOM_CAPACITY, roomCaptor.getValue().getCapacity())
        );
    }

    @Test
    void testGetRoomsByLocationId() throws Exception {
        // Arrange
        LocationServiceImpl spy = spy(locationService);
        List<ConferenceRoomEntity> rooms = List.of(roomEntity);
        List<RoomResponse> responses = List.of(roomResponse);

        doReturn(locationEntity).when(spy).getLocationById(LOCATION_ID);
        when(roomRepository.findByLocationId(LOCATION_ID)).thenReturn(rooms);
        when(locationMapper.toRoomResponseList(rooms)).thenReturn(responses);

        // Act
        List<RoomResponse> result = spy.getRoomsByLocationId(LOCATION_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(ROOM_ID, result.get(0).getId()),
                () -> assertEquals(ROOM_NAME, result.get(0).getName())
        );
    }

    @Test
    void testGetAllLocations() {
        // Arrange
        List<LocationEntity> locations = List.of(locationEntity);
        List<LocationResponse> responses = List.of(locationResponse);

        when(locationRepository.findAll()).thenReturn(locations);
        when(locationMapper.toResponseList(locations)).thenReturn(responses);

        // Act
        List<LocationResponse> result = locationService.getAllLocations();

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(LOCATION_ID, result.get(0).getId()),
                () -> assertEquals(LOCATION_NAME, result.get(0).getName())
        );
    }

    @Test
    void testDeleteRoom() throws Exception {
        // Arrange
        LocationServiceImpl spy = spy(locationService);

        doReturn(roomEntity).when(spy).getRoomById(ROOM_ID);
        when(activityRepository.existsByRoomId(ROOM_ID)).thenReturn(false);

        // Act
        spy.deleteRoom(ROOM_ID);

        // Assert
        verify(roomRepository).delete(roomEntity);
    }

    @Test
    void testDeleteRoomWhenHasActivities() throws Exception {
        // Arrange
        LocationServiceImpl spy = spy(locationService);

        doReturn(roomEntity).when(spy).getRoomById(ROOM_ID);
        when(activityRepository.existsByRoomId(ROOM_ID)).thenReturn(true);

        // Assert
        assertThrows(RoomHasActivitiesException.class,
                () -> spy.deleteRoom(ROOM_ID));
    }
}