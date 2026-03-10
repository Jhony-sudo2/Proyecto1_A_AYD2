package com.ayd2.congress.services;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.compositePrimaryKeys.AttendanceId;
import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.attendance.AttendanceResponse;
import com.ayd2.congress.dtos.attendance.NewAttendanceRequest;
import com.ayd2.congress.exceptions.ActivityAlreadyEndendException;
import com.ayd2.congress.exceptions.ActivityFullException;
import com.ayd2.congress.exceptions.ActivityNotStartedException;
import com.ayd2.congress.exceptions.CongressNotStartedException;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.ActivityMapper;
import com.ayd2.congress.mappers.AttendanceMapper;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Attendance.AttendanceEntity;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.AttendanceType;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.Attendance.AttendanceRepository;
import com.ayd2.congress.services.Congress.CongressService;
import com.ayd2.congress.services.Inscription.InscriptionService;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.activity.ActivityService;
import com.ayd2.congress.services.attendance.AttendanceServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long ACTIVITY_ID = 2L;
    private static final Long CONGRESS_ID = 3L;

    private static final String USER_IDENTIFICATION = "1234567890101";
    private static final String USER_NAME = "Juan Perez";
    private static final String ACTIVITY_NAME = "Taller Spring";
    private static final String CONGRESS_NAME = "Congress 2026";

    private static final LocalDateTime REQUEST_DATE = LocalDateTime.of(2026, 3, 10, 10, 0);
    private static final LocalDateTime CONGRESS_START = LocalDateTime.of(2026, 3, 9, 8, 0);
    private static final LocalDateTime ACTIVITY_START = LocalDateTime.of(2026, 3, 10, 9, 0);
    private static final LocalDateTime ACTIVITY_END = LocalDateTime.of(2026, 3, 10, 12, 0);

    @Mock
    private ActivityService activityService;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private UserService userService;
    @Mock
    private InscriptionService inscriptionService;
    @Mock
    private AttendanceMapper attendanceMapper;
    @Mock
    private CongressService congressService;
    @Mock
    private ActivityMapper activityMapper;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private UserEntity userEntity;
    private CongressEntity congressEntity;
    private ActivityEntity activityEntity;
    private AttendanceEntity attendanceEntity;
    private AttendanceResponse attendanceResponse;
    private ActivityResponse activityResponse;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setIdentification(USER_IDENTIFICATION);
        userEntity.setName(USER_NAME);

        congressEntity = new CongressEntity();
        congressEntity.setId(CONGRESS_ID);
        congressEntity.setName(CONGRESS_NAME);
        congressEntity.setStartDate(CONGRESS_START);

        activityEntity = new ActivityEntity();
        activityEntity.setId(ACTIVITY_ID);
        activityEntity.setName(ACTIVITY_NAME);
        activityEntity.setType(ActivityType.CONFERENCE);
        activityEntity.setStartDate(ACTIVITY_START);
        activityEntity.setEndDate(ACTIVITY_END);
        activityEntity.setCapacity(10);
        activityEntity.setCongress(congressEntity);

        attendanceEntity = new AttendanceEntity();
        attendanceEntity.setId(new AttendanceId(USER_ID, ACTIVITY_ID, AttendanceType.ATTENDANCE));
        attendanceEntity.setUser(userEntity);
        attendanceEntity.setActivity(activityEntity);
        attendanceEntity.setType(AttendanceType.ATTENDANCE);

        attendanceResponse = new AttendanceResponse(USER_ID, USER_NAME, AttendanceType.ATTENDANCE);

        activityResponse = new ActivityResponse(
                ACTIVITY_ID,
                ACTIVITY_NAME,
                "desc",
                ACTIVITY_START,
                ACTIVITY_END,
                ActivityType.WORKSHOP,
                10,
                1L,
                "Room A",
                new String[] { "Speaker 1" }
        );
    }

    @Test
    void testCreateAttendance() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                REQUEST_DATE,
                AttendanceType.ATTENDANCE
        );

        ArgumentCaptor<AttendanceEntity> attendanceCaptor = ArgumentCaptor.forClass(AttendanceEntity.class);

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(userService.getByIdentification(USER_IDENTIFICATION)).thenReturn(userEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.ATTENDANCE))
                .thenReturn(false);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(true);

        // Act
        attendanceService.createAttendance(request);

        // Assert
        assertAll(
                () -> verify(attendanceRepository).save(attendanceCaptor.capture()),
                () -> assertEquals(USER_ID, attendanceCaptor.getValue().getId().getUserId()),
                () -> assertEquals(ACTIVITY_ID, attendanceCaptor.getValue().getId().getActivityId()),
                () -> assertEquals(AttendanceType.ATTENDANCE, attendanceCaptor.getValue().getId().getType()),
                () -> assertEquals(USER_ID, attendanceCaptor.getValue().getUser().getId()),
                () -> assertEquals(ACTIVITY_ID, attendanceCaptor.getValue().getActivity().getId()),
                () -> assertEquals(AttendanceType.ATTENDANCE, attendanceCaptor.getValue().getType())
        );
    }

    @Test
    void testCreateAttendanceWhenDuplicated() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                REQUEST_DATE,
                AttendanceType.ATTENDANCE
        );

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(userService.getByIdentification(USER_IDENTIFICATION)).thenReturn(userEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.ATTENDANCE))
                .thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> attendanceService.createAttendance(request));
    }

    @Test
    void testCreateAttendanceWhenUserIsNotInscribedInCongress() throws Exception {
        // Arrange
        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                REQUEST_DATE,
                AttendanceType.ATTENDANCE
        );

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(userService.getByIdentification(USER_IDENTIFICATION)).thenReturn(userEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.ATTENDANCE))
                .thenReturn(false);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(false);

        // Assert
        assertThrows(NotFoundException.class,
                () -> attendanceService.createAttendance(request));
    }

    @Test
    void testCreateAttendanceWorkshopWhenUserIsNotEnrolledInWorkshop() throws Exception {
        // Arrange
        AttendanceServiceImpl spy = spy(attendanceService);

        activityEntity.setType(ActivityType.WORKSHOP);

        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                REQUEST_DATE,
                AttendanceType.ATTENDANCE
        );

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(userService.getByIdentification(USER_IDENTIFICATION)).thenReturn(userEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.ATTENDANCE))
                .thenReturn(false);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(true);
        doReturn(false).when(spy).isEnrolledInWorkshop(USER_ID, ACTIVITY_ID);

        // Assert
        assertThrows(NotFoundException.class,
                () -> spy.createAttendance(request));
    }

    @Test
    void testCreateAttendanceWorkshopInscriptionShouldVerifyCapacity() throws Exception {
        // Arrange
        AttendanceServiceImpl spy = spy(attendanceService);
        ArgumentCaptor<AttendanceEntity> attendanceCaptor = ArgumentCaptor.forClass(AttendanceEntity.class);

        activityEntity.setType(ActivityType.WORKSHOP);

        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                REQUEST_DATE,
                AttendanceType.WORKSHOPINSCRIPTION
        );

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(userService.getByIdentification(USER_IDENTIFICATION)).thenReturn(userEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.WORKSHOPINSCRIPTION))
                .thenReturn(false);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(true);
        doReturn(true).when(spy).verifyCapacity(ACTIVITY_ID);

        // Act
        spy.createAttendance(request);

        // Assert
        assertAll(
                () -> verify(spy).verifyCapacity(ACTIVITY_ID),
                () -> verify(attendanceRepository).save(attendanceCaptor.capture()),
                () -> assertEquals(AttendanceType.WORKSHOPINSCRIPTION, attendanceCaptor.getValue().getType())
        );
    }

    @Test
    void testCreateAttendanceWhenCongressHasNotStarted() throws Exception {
        // Arrange
        congressEntity.setStartDate(REQUEST_DATE.plusDays(1));

        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                REQUEST_DATE,
                AttendanceType.ATTENDANCE
        );

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(userService.getByIdentification(USER_IDENTIFICATION)).thenReturn(userEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.ATTENDANCE))
                .thenReturn(false);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(true);

        // Assert
        assertThrows(CongressNotStartedException.class,
                () -> attendanceService.createAttendance(request));
    }

    @Test
    void testCreateAttendanceWhenActivityHasNotStarted() throws Exception {
        // Arrange
        activityEntity.setStartDate(REQUEST_DATE.plusHours(2));

        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                REQUEST_DATE,
                AttendanceType.ATTENDANCE
        );

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(userService.getByIdentification(USER_IDENTIFICATION)).thenReturn(userEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.ATTENDANCE))
                .thenReturn(false);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(true);

        // Assert
        assertThrows(ActivityNotStartedException.class,
                () -> attendanceService.createAttendance(request));
    }

    @Test
    void testCreateAttendanceWhenActivityAlreadyEnded() throws Exception {
        // Arrange
        activityEntity.setEndDate(REQUEST_DATE.minusMinutes(1));

        NewAttendanceRequest request = new NewAttendanceRequest(
                ACTIVITY_ID,
                USER_IDENTIFICATION,
                REQUEST_DATE,
                AttendanceType.ATTENDANCE
        );

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(userService.getByIdentification(USER_IDENTIFICATION)).thenReturn(userEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.ATTENDANCE))
                .thenReturn(false);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(true);

        // Assert
        assertThrows(ActivityAlreadyEndendException.class,
                () -> attendanceService.createAttendance(request));
    }

    @Test
    void testVerifyCapacity() throws Exception {
        // Arrange
        activityEntity.setType(ActivityType.WORKSHOP);
        activityEntity.setCapacity(10);

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(attendanceRepository.countByActivityIdAndType(ACTIVITY_ID, AttendanceType.WORKSHOPINSCRIPTION))
                .thenReturn(5L);

        // Act
        boolean result = attendanceService.verifyCapacity(ACTIVITY_ID);

        // Assert
        assertTrue(result);
    }

    @Test
    void testVerifyCapacityWhenActivityIsNotWorkshop() throws Exception {
        // Arrange
        activityEntity.setType(ActivityType.CONFERENCE);

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);

        // Assert
        assertThrows(NotFoundException.class,
                () -> attendanceService.verifyCapacity(ACTIVITY_ID));
    }

    @Test
    void testVerifyCapacityWhenCapacityIsZero() throws Exception {
        // Arrange
        activityEntity.setType(ActivityType.WORKSHOP);
        activityEntity.setCapacity(0);

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);

        // Assert
        assertThrows(ActivityFullException.class,
                () -> attendanceService.verifyCapacity(ACTIVITY_ID));
    }

    @Test
    void testVerifyCapacityWhenActivityIsFull() throws Exception {
        // Arrange
        activityEntity.setType(ActivityType.WORKSHOP);
        activityEntity.setCapacity(5);

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(attendanceRepository.countByActivityIdAndType(ACTIVITY_ID, AttendanceType.WORKSHOPINSCRIPTION))
                .thenReturn(5L);

        // Assert
        assertThrows(ActivityFullException.class,
                () -> attendanceService.verifyCapacity(ACTIVITY_ID));
    }

    @Test
    void testGetAttendanceByUserId() throws Exception {
        // Arrange
        List<AttendanceEntity> entities = List.of(attendanceEntity);
        List<AttendanceResponse> responses = List.of(attendanceResponse);

        when(attendanceRepository.findByUserId(USER_ID)).thenReturn(entities);
        when(attendanceMapper.toResponseList(entities)).thenReturn(responses);

        // Act
        List<AttendanceResponse> result = attendanceService.getAttendanceByUserId(USER_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(USER_ID, result.get(0).getUserId()),
                () -> assertEquals(AttendanceType.ATTENDANCE, result.get(0).getType())
        );
    }

    @Test
    void testGetAttendanceByUserIdWhenEmpty() {
        // Arrange
        when(attendanceRepository.findByUserId(USER_ID)).thenReturn(List.of());

        // Assert
        assertThrows(NotFoundException.class,
                () -> attendanceService.getAttendanceByUserId(USER_ID));
    }

    @Test
    void testGetAttendanceByActivityId() throws Exception {
        // Arrange
        List<AttendanceEntity> entities = List.of(attendanceEntity);
        List<AttendanceResponse> responses = List.of(attendanceResponse);

        when(attendanceRepository.findByActivityIdAndType(ACTIVITY_ID, AttendanceType.ATTENDANCE)).thenReturn(entities);
        when(attendanceMapper.toResponseList(entities)).thenReturn(responses);

        // Act
        List<AttendanceResponse> result = attendanceService.getAttendanceByActivityId(ACTIVITY_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(USER_ID, result.get(0).getUserId()),
                () -> assertEquals(AttendanceType.ATTENDANCE, result.get(0).getType())
        );
    }

    @Test
    void testGetAttendanceByActivityIdWhenEmpty() {
        // Arrange
        when(attendanceRepository.findByActivityIdAndType(ACTIVITY_ID, AttendanceType.ATTENDANCE)).thenReturn(List.of());

        // Assert
        assertThrows(NotFoundException.class,
                () -> attendanceService.getAttendanceByActivityId(ACTIVITY_ID));
    }

    @Test
    void testIsEnrolledInWorkshop() throws Exception {
        // Arrange
        activityEntity.setType(ActivityType.WORKSHOP);

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);
        when(attendanceRepository.existsByActivityIdAndUserIdAndType(ACTIVITY_ID, USER_ID, AttendanceType.WORKSHOPINSCRIPTION))
                .thenReturn(true);

        // Act
        boolean result = attendanceService.isEnrolledInWorkshop(USER_ID, ACTIVITY_ID);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsEnrolledInWorkshopWhenActivityIsNotWorkshop() throws Exception {
        // Arrange
        activityEntity.setType(ActivityType.CONFERENCE);

        when(activityService.getActivityById(ACTIVITY_ID)).thenReturn(activityEntity);

        // Assert
        assertThrows(NotFoundException.class,
                () -> attendanceService.isEnrolledInWorkshop(USER_ID, ACTIVITY_ID));
    }

    @Test
    void testGetAttendanceByUserIdAndCongressId() throws Exception {
        // Arrange
        List<AttendanceEntity> entities = List.of(attendanceEntity);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(attendanceRepository.findAllByUserIdAndActivityCongressIdAndType(USER_ID, CONGRESS_ID, AttendanceType.WORKSHOPINSCRIPTION))
                .thenReturn(entities);

        // Act
        List<AttendanceEntity> result = attendanceService.getAttendanceByUserIdAndCongressId(
                USER_ID,
                CONGRESS_ID,
                AttendanceType.WORKSHOPINSCRIPTION
        );

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetByUserIdAndCongressId() throws Exception {
        // Arrange
        AttendanceServiceImpl spy = spy(attendanceService);

        AttendanceEntity duplicateAttendance = new AttendanceEntity();
        duplicateAttendance.setId(new AttendanceId(USER_ID, ACTIVITY_ID, AttendanceType.WORKSHOPINSCRIPTION));
        duplicateAttendance.setUser(userEntity);
        duplicateAttendance.setActivity(activityEntity);
        duplicateAttendance.setType(AttendanceType.WORKSHOPINSCRIPTION);

        List<AttendanceEntity> entities = List.of(attendanceEntity, duplicateAttendance);
        List<ActivityResponse> responses = List.of(activityResponse);

        doReturn(entities).when(spy).getAttendanceByUserIdAndCongressId(
                USER_ID,
                CONGRESS_ID,
                AttendanceType.WORKSHOPINSCRIPTION
        );

        when(activityMapper.toActivityResponseList(any())).thenReturn(responses);

        // Act
        List<ActivityResponse> result = spy.getByUserIdAndCongressId(USER_ID, CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(ACTIVITY_ID, result.get(0).getId()),
                () -> verify(activityMapper).toActivityResponseList(any())
        );
    }
}