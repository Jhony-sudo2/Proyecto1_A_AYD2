package com.ayd2.congress.controllers;
import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.attendance.AttendanceResponse;
import com.ayd2.congress.dtos.attendance.NewAttendanceRequest;
import com.ayd2.congress.exceptionhandler.ControllerExceptionHandler;
import com.ayd2.congress.exceptions.ActivityAlreadyEndendException;
import com.ayd2.congress.exceptions.ActivityNotStartedException;
import com.ayd2.congress.exceptions.CongressNotStartedException;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.AttendanceType;
import com.ayd2.congress.security.config.Security;
import com.ayd2.congress.security.jwtfilter.JwtAuthenticationFilter;
import com.ayd2.congress.services.attendance.AttendanceService;
import java.time.LocalDateTime;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AttendanceController.class,
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
public class AttendanceControllerTest extends CommonMvcTest {

    private static final Long USER_ID = 1L;
    private static final Long ACTIVITY_ID = 2L;
    private static final Long CONGRESS_ID = 3L;

    private static final String USER_IDENTIFICATION = "1234567890101";
    private static final String USER_NAME = "Juan Perez";
    private static final String ACTIVITY_NAME = "Workshop Spring";
    private static final String ROOM_NAME = "Room A";
    private static final String DESCRIPTION = "Workshop description";

    private static final LocalDateTime DATE = LocalDateTime.of(2026, 3, 10, 10, 0);
    private static final LocalDateTime START_DATE = LocalDateTime.of(2026, 3, 10, 10, 0);
    private static final LocalDateTime END_DATE = LocalDateTime.of(2026, 3, 10, 12, 0);

    @MockitoBean
    private AttendanceService attendanceService;

    @Test
    public void testCreateAttendance() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                DATE,
                AttendanceType.ATTENDANCE
        );

        // Act
        mockMvc.perform(post("/atteendances")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isCreated());

        verify(attendanceService).createAttendance(request);
    }

    @Test
    public void testCreateAttendanceWhenDuplicated() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                DATE,
                AttendanceType.ATTENDANCE
        );

        doThrow(new DuplicatedEntityException("Attendance already exists"))
                .when(attendanceService).createAttendance(request);

        // Act
        mockMvc.perform(post("/atteendances")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(attendanceService).createAttendance(request);
    }

    @Test
    public void testCreateAttendanceWhenNotFound() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                DATE,
                AttendanceType.ATTENDANCE
        );

        doThrow(new NotFoundException("User is not inscribed in the congress"))
                .when(attendanceService).createAttendance(request);

        // Act
        mockMvc.perform(post("/atteendances")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isNotFound());

        verify(attendanceService).createAttendance(request);
    }

    @Test
    public void testCreateAttendanceWhenActivityAlreadyEnded() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                DATE,
                AttendanceType.ATTENDANCE
        );

        doThrow(new ActivityAlreadyEndendException("Activity has already ended"))
                .when(attendanceService).createAttendance(request);

        // Act
        mockMvc.perform(post("/atteendances")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(attendanceService).createAttendance(request);
    }

    @Test
    public void testCreateAttendanceWhenCongressNotStarted() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                DATE,
                AttendanceType.ATTENDANCE
        );

        doThrow(new CongressNotStartedException("Congress has not started yet"))
                .when(attendanceService).createAttendance(request);

        // Act
        mockMvc.perform(post("/atteendances")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(attendanceService).createAttendance(request);
    }

    @Test
    public void testCreateAttendanceWhenActivityNotStarted() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                DATE,
                AttendanceType.ATTENDANCE
        );

        doThrow(new ActivityNotStartedException("Activity has not started yet"))
                .when(attendanceService).createAttendance(request);

        // Act
        mockMvc.perform(post("/atteendances")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Assert
                .andExpect(status().isConflict());

        verify(attendanceService).createAttendance(request);
    }

    @Test
    public void testGetAttendanceByUserId() throws Exception {
        // Arrange
        AttendanceResponse response = new AttendanceResponse(
                USER_ID,
                USER_NAME,
                AttendanceType.ATTENDANCE
        );

        when(attendanceService.getAttendanceByUserId(USER_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/atteendances/users/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });
    }

    @Test
    public void testGetAttendanceByUserIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("No attendance found for the user"))
                .when(attendanceService).getAttendanceByUserId(USER_ID);

        // Act
        mockMvc.perform(get("/atteendances/users/{userId}", USER_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(attendanceService).getAttendanceByUserId(USER_ID);
    }

    @Test
    public void testGetWorkShopInscriptionByUserAndCongressId() throws Exception {
        // Arrange
        ActivityResponse response = new ActivityResponse(
                ACTIVITY_ID,
                ACTIVITY_NAME,
                DESCRIPTION,
                START_DATE,
                END_DATE,
                ActivityType.WORKSHOP,
                50,
                10L,
                ROOM_NAME,
                new String[]{USER_NAME}
        );

        when(attendanceService.getByUserIdAndCongressId(USER_ID, CONGRESS_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/atteendances/users/{userId}/congress/{congressId}", USER_ID, CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });
    }

    @Test
    public void testGetWorkShopInscriptionByUserAndCongressIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("User not found"))
                .when(attendanceService).getByUserIdAndCongressId(USER_ID, CONGRESS_ID);

        // Act
        mockMvc.perform(get("/atteendances/users/{userId}/congress/{congressId}", USER_ID, CONGRESS_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(attendanceService).getByUserIdAndCongressId(USER_ID, CONGRESS_ID);
    }

    @Test
    public void testGetAttendanceByActivityId() throws Exception {
        // Arrange
        AttendanceResponse response = new AttendanceResponse(
                USER_ID,
                USER_NAME,
                AttendanceType.ATTENDANCE
        );

        when(attendanceService.getAttendanceByActivityId(ACTIVITY_ID)).thenReturn(List.of(response));

        // Act
        mockMvc.perform(get("/atteendances/activities/{activityId}", ACTIVITY_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Assertions.assertThat(json)
                            .isEqualTo(objectMapper.writeValueAsString(List.of(response)));
                });
    }

    @Test
    public void testGetAttendanceByActivityIdWhenNotFound() throws Exception {
        // Arrange
        doThrow(new NotFoundException("No attendance found for the activity"))
                .when(attendanceService).getAttendanceByActivityId(ACTIVITY_ID);

        // Act
        mockMvc.perform(get("/atteendances/activities/{activityId}", ACTIVITY_ID)
                .accept(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(status().isNotFound());

        verify(attendanceService).getAttendanceByActivityId(ACTIVITY_ID);
    }
}
