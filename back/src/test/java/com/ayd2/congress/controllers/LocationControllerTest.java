package com.ayd2.congress.controllers;

import com.ayd2.congress.dtos.Location.LocationResponse;
import com.ayd2.congress.dtos.Location.NewLocationRequest;
import com.ayd2.congress.dtos.Location.NewRoomRequest;
import com.ayd2.congress.dtos.Location.RoomResponse;
import com.ayd2.congress.dtos.Location.UpdateRoom;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.exceptions.RoomHasActivitiesException;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.Location.LocationService;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.security.autoconfigure.web.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = LocationController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class,
                UserDetailsServiceAutoConfiguration.class
        },
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = Security.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import({ControllerExceptionHandler.class, CommonMvcTest.TestConfig.class})
public class LocationControllerTest extends CommonMvcTest {

    private static final Long LOCATION_ID = 1L;
    private static final Long ROOM_ID = 10L;

    private static final String LOCATION_NAME = "Centro de Convenciones";
    private static final String ADDRESS = "Zona 1";
    private static final String CITY = "Guatemala";
    private static final String COUNTRY = "Guatemala";

    private static final String ROOM_NAME = "Salon A";
    private static final Integer ROOM_CAPACITY = 100;
    private static final String ROOM_EQUIPMENT = "Projector";
    private static final String ROOM_DESCRIPTION = "Main room";

    @MockitoBean
    private LocationService locationService;

    @Test
    public void testCreateLocation() throws Exception {
        // Arrange
        NewLocationRequest request = new NewLocationRequest(
                LOCATION_NAME,
                ADDRESS,
                CITY,
                COUNTRY
        );

        LocationResponse response = new LocationResponse(
                LOCATION_ID,
                LOCATION_NAME,
                ADDRESS,
                CITY,
                COUNTRY
        );

        when(locationService.createLocation(request)).thenReturn(response);

        // Act
        mockMvc.perform(post("/locations")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(locationService).createLocation(request);
    }

    @Test
    public void testGetAllLocations() throws Exception {
        // Arrange
        LocationResponse response = new LocationResponse(
                LOCATION_ID,
                LOCATION_NAME,
                ADDRESS,
                CITY,
                COUNTRY
        );

        when(locationService.getAllLocations()).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/locations")
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(locationService).getAllLocations();
    }

    @Test
    public void testCreateRoom() throws Exception {
        // Arrange
        NewRoomRequest request = new NewRoomRequest(
                ROOM_NAME,
                ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );

        RoomResponse response = new RoomResponse(
                ROOM_ID,
                ROOM_NAME,
                ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );

        when(locationService.createRoom(request, LOCATION_ID)).thenReturn(response);

        // Act
        mockMvc.perform(post("/locations/{locationId}/rooms", LOCATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(locationService).createRoom(request, LOCATION_ID);
    }

    @Test
    public void testCreateRoomWhenNotFound() throws Exception {
        // Arrange
        NewRoomRequest request = new NewRoomRequest(
                ROOM_NAME,
                ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );

        doThrow(new NotFoundException("Location not found"))
                .when(locationService).createRoom(request, LOCATION_ID);

        // Act
        mockMvc.perform(post("/locations/{locationId}/rooms", LOCATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(locationService).createRoom(request, LOCATION_ID);
    }

    @Test
    public void testCreateRoomWhenDuplicated() throws Exception {
        // Arrange
        NewRoomRequest request = new NewRoomRequest(
                ROOM_NAME,
                ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );

        doThrow(new DuplicatedEntityException("Room already exists"))
                .when(locationService).createRoom(request, LOCATION_ID);

        // Act
        mockMvc.perform(post("/locations/{locationId}/rooms", LOCATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(locationService).createRoom(request, LOCATION_ID);
    }

    @Test
    public void testGetRoomsByLocationId() throws Exception {
        // Arrange
        RoomResponse response = new RoomResponse(
                ROOM_ID,
                ROOM_NAME,
                ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );

        when(locationService.getRoomsByLocationId(LOCATION_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/locations/{locationId}/rooms", LOCATION_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });

        verify(locationService).getRoomsByLocationId(LOCATION_ID);
    }

    @Test
    public void testGetRoomsByLocationIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Location not found"))
                .when(locationService).getRoomsByLocationId(LOCATION_ID);

        // Act
        mockMvc.perform(get("/locations/{locationId}/rooms", LOCATION_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(locationService).getRoomsByLocationId(LOCATION_ID);
    }

    @Test
    public void testGetLocationById() throws Exception {
        // Arrange
        LocationResponse response = new LocationResponse(
                LOCATION_ID,
                LOCATION_NAME,
                ADDRESS,
                CITY,
                COUNTRY
        );

        when(locationService.getLocationResponseById(LOCATION_ID)).thenReturn(response);

        // Act
        mockMvc.perform(get("/locations/{id}", LOCATION_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(locationService).getLocationResponseById(LOCATION_ID);
    }

    @Test
    public void testGetLocationByIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Location not found"))
                .when(locationService).getLocationResponseById(LOCATION_ID);

        // Act
        mockMvc.perform(get("/locations/{id}", LOCATION_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(locationService).getLocationResponseById(LOCATION_ID);
    }

    @Test
    public void testGetRoomById() throws Exception {
        // Arrange
        RoomResponse response = new RoomResponse(
                ROOM_ID,
                ROOM_NAME,
                ROOM_CAPACITY,
                ROOM_EQUIPMENT,
                ROOM_DESCRIPTION
        );

        when(locationService.getRoomResponseById(ROOM_ID)).thenReturn(response);

        // Act
        mockMvc.perform(get("/locations/rooms/{id}", ROOM_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(locationService).getRoomResponseById(ROOM_ID);
    }

    @Test
    public void testGetRoomByIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Room not found"))
                .when(locationService).getRoomResponseById(ROOM_ID);

        // Act
        mockMvc.perform(get("/locations/rooms/{id}", ROOM_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(locationService).getRoomResponseById(ROOM_ID);
    }

    @Test
    public void testUpdateRoom() throws Exception {
        // Arrange
        UpdateRoom request = new UpdateRoom(
                150,
                "Updated description",
                "Salon B",
                "TV"
        );

        RoomResponse response = new RoomResponse(
                ROOM_ID,
                "Salon B",
                150,
                "TV",
                "Updated description"
        );

        when(locationService.updateRoom(ROOM_ID, request)).thenReturn(response);

        // Act
        mockMvc.perform(put("/locations/rooms/{id}", ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(response));
                });

        verify(locationService).updateRoom(ROOM_ID, request);
    }

    @Test
    public void testUpdateRoomWhenNotFound() throws Exception {
        // Arrange
        UpdateRoom request = new UpdateRoom(
                150,
                "Updated description",
                "Salon B",
                "TV"
        );

        doThrow(new NotFoundException("Room not found"))
                .when(locationService).updateRoom(ROOM_ID, request);

        // Act
        mockMvc.perform(put("/locations/rooms/{id}", ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(locationService).updateRoom(ROOM_ID, request);
    }

    @Test
    public void testUpdateRoomWhenDuplicated() throws Exception {
        // Arrange
        UpdateRoom request = new UpdateRoom(
                150,
                "Updated description",
                "Salon B",
                "TV"
        );

        doThrow(new DuplicatedEntityException("Room already exists"))
                .when(locationService).updateRoom(ROOM_ID, request);

        // Act
        mockMvc.perform(put("/locations/rooms/{id}", ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(locationService).updateRoom(ROOM_ID, request);
    }

    @Test
    public void testDeleteRoom() throws Exception {
        // Act
        mockMvc.perform(delete("/locations/rooms/{id}", ROOM_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk());

        verify(locationService).deleteRoom(ROOM_ID);
    }

    @Test
    public void testDeleteRoomWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("Room not found"))
                .when(locationService).deleteRoom(ROOM_ID);

        // Act
        mockMvc.perform(delete("/locations/rooms/{id}", ROOM_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(locationService).deleteRoom(ROOM_ID);
    }

    @Test
    public void testDeleteRoomWhenHasActivities() throws Exception {
        // Arrange
        doThrow(new RoomHasActivitiesException("EL SALON TIENE ACTIVIDADES ASOCIADAS"))
                .when(locationService).deleteRoom(ROOM_ID);

        // Act
        mockMvc.perform(delete("/locations/rooms/{id}", ROOM_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isConflict());

        verify(locationService).deleteRoom(ROOM_ID);
    }
}