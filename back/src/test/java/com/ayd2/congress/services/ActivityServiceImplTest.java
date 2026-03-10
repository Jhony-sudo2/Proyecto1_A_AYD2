package com.ayd2.congress.services;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayd2.congress.compositePrimaryKeys.SpeakerId;
import com.ayd2.congress.dtos.acitivty.ActivityResponse;
import com.ayd2.congress.dtos.acitivty.NewActivityGuest;
import com.ayd2.congress.dtos.acitivty.NewActivityRequest;
import com.ayd2.congress.dtos.acitivty.NewProposalRequest;
import com.ayd2.congress.dtos.acitivty.ProposalResponse;
import com.ayd2.congress.dtos.acitivty.UpdateActivity;
import com.ayd2.congress.dtos.acitivty.UpdateProposal;
import com.ayd2.congress.exceptions.ActivityHasAttendancesException;
import com.ayd2.congress.exceptions.DuplicatedEntityException;
import com.ayd2.congress.exceptions.InvalidDateRangeException;
import com.ayd2.congress.exceptions.NotFoundException;
import com.ayd2.congress.mappers.ActivityMapper;
import com.ayd2.congress.models.Activities.ActivityEntity;
import com.ayd2.congress.models.Activities.ProposalEntity;
import com.ayd2.congress.models.Activities.SpeakerEntity;
import com.ayd2.congress.models.Congress.ConferenceRoomEntity;
import com.ayd2.congress.models.Congress.CongressEntity;
import com.ayd2.congress.models.Enums.ActivityType;
import com.ayd2.congress.models.Enums.ProposalState;
import com.ayd2.congress.models.User.UserEntity;
import com.ayd2.congress.repositories.Activity.ActivityRepository;
import com.ayd2.congress.repositories.Activity.SpeakerRepository;
import com.ayd2.congress.repositories.Attendance.AttendanceRepository;
import com.ayd2.congress.repositories.Attendance.ProposalRepository;
import com.ayd2.congress.services.Congress.CongressService;
import com.ayd2.congress.services.Inscription.InscriptionService;
import com.ayd2.congress.services.Location.LocationService;
import com.ayd2.congress.services.User.UserService;
import com.ayd2.congress.services.activity.ActivityServiceImpl;

@ExtendWith(MockitoExtension.class)
public class ActivityServiceImplTest {

    private static final Long ACTIVITY_ID = 1L;
    private static final Long PROPOSAL_ID = 2L;
    private static final Long ROOM_ID = 3L;
    private static final Long CONGRESS_ID = 4L;
    private static final Long USER_ID = 5L;
    private static final Long GUEST_ROLE_ID = 2L;
    private static final Long CONFERENCE_ROLE_ID = 3L;

    private static final String ACTIVITY_NAME = "Spring Boot Conference";
    private static final String UPDATED_ACTIVITY_NAME = "Updated Activity";
    private static final String PROPOSAL_NAME = "Talk Proposal";
    private static final String DESCRIPTION = "Activity Description";
    private static final String USER_NAME = "Juan Perez";
    private static final String CONGRESS_NAME = "Congress 2026";
    private static final String ROOM_NAME = "Room A";

    private static final LocalDateTime CONGRESS_START = LocalDateTime.of(2026, 3, 10, 8, 0);
    private static final LocalDateTime CONGRESS_END = LocalDateTime.of(2026, 3, 12, 18, 0);
    private static final LocalDateTime ACTIVITY_START = LocalDateTime.of(2026, 3, 10, 10, 0);
    private static final LocalDateTime ACTIVITY_END = LocalDateTime.of(2026, 3, 10, 12, 0);
    private static final LocalDateTime UPDATED_START = LocalDateTime.of(2026, 3, 10, 14, 0);
    private static final LocalDateTime UPDATED_END = LocalDateTime.of(2026, 3, 10, 16, 0);

    @Mock
    private LocationService locationService;
    @Mock
    private ProposalRepository proposalRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private CongressService congressService;
    @Mock
    private UserService userService;
    @Mock
    private InscriptionService inscriptionService;
    @Mock
    private ActivityMapper activityMapper;
    @Mock
    private SpeakerRepository speakerRepository;
    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private ActivityServiceImpl activityService;

    private ProposalEntity proposalEntity;
    private ConferenceRoomEntity roomEntity;
    private CongressEntity congressEntity;
    private UserEntity userEntity;
    private ActivityEntity activityEntity;
    private SpeakerEntity speakerEntity;
    private ActivityResponse activityResponse;
    private ProposalResponse proposalResponse;

    @BeforeEach
    void setUp() {
        congressEntity = new CongressEntity();
        congressEntity.setId(CONGRESS_ID);
        congressEntity.setName(CONGRESS_NAME);
        congressEntity.setStartDate(CONGRESS_START);
        congressEntity.setEndDate(CONGRESS_END);

        userEntity = new UserEntity();
        userEntity.setId(USER_ID);
        userEntity.setName(USER_NAME);

        roomEntity = new ConferenceRoomEntity();
        roomEntity.setId(ROOM_ID);
        roomEntity.setName(ROOM_NAME);

        proposalEntity = new ProposalEntity();
        proposalEntity.setId(PROPOSAL_ID);
        proposalEntity.setName(PROPOSAL_NAME);
        proposalEntity.setDescription(DESCRIPTION);
        proposalEntity.setState(ProposalState.APPROVED);
        proposalEntity.setType(ActivityType.CONFERENCE);
        proposalEntity.setUsed(false);
        proposalEntity.setCongress(congressEntity);
        proposalEntity.setUser(userEntity);

        activityEntity = new ActivityEntity();
        activityEntity.setId(ACTIVITY_ID);
        activityEntity.setName(ACTIVITY_NAME);
        activityEntity.setStartDate(ACTIVITY_START);
        activityEntity.setEndDate(ACTIVITY_END);
        activityEntity.setCongress(congressEntity);
        activityEntity.setRoom(roomEntity);

        speakerEntity = new SpeakerEntity();
        speakerEntity.setId(new SpeakerId(ACTIVITY_ID, USER_ID));
        speakerEntity.setActivity(activityEntity);
        speakerEntity.setUser(userEntity);

        activityResponse = new ActivityResponse(
                ACTIVITY_ID,
                ACTIVITY_NAME,
                DESCRIPTION,
                ACTIVITY_START,
                ACTIVITY_END,
                ActivityType.CONFERENCE,
                100,
                ROOM_ID,
                ROOM_NAME,
                new String[] { USER_NAME }
        );

        proposalResponse = new ProposalResponse(
                PROPOSAL_ID,
                PROPOSAL_NAME,
                CONGRESS_NAME,
                USER_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE,
                ProposalState.APPROVED
        );
    }

    @Test
    void testCreateActivity() throws Exception {
        // Arrange
        NewActivityRequest request = new NewActivityRequest(
                ACTIVITY_NAME,
                ROOM_ID,
                PROPOSAL_ID,
                ACTIVITY_START,
                ACTIVITY_END,
                100
        );

        ArgumentCaptor<ActivityEntity> activityCaptor = ArgumentCaptor.forClass(ActivityEntity.class);
        ArgumentCaptor<SpeakerEntity> speakerCaptor = ArgumentCaptor.forClass(SpeakerEntity.class);

        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.of(proposalEntity));
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(activityRepository.existsOverlap(ROOM_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(false);
        when(activityMapper.toEntity(request)).thenReturn(activityEntity);
        when(speakerRepository.existsByUserIdAndActivityId(USER_ID, ACTIVITY_ID)).thenReturn(false);
        when(activityMapper.toActivityResponse(activityEntity)).thenReturn(activityResponse);

        // Act
        ActivityResponse result = activityService.createActivity(request);

        // Assert
        assertAll(
                () -> verify(activityRepository).save(activityCaptor.capture()),
                () -> verify(speakerRepository).save(speakerCaptor.capture()),
                () -> verify(inscriptionService).enroll(USER_ID, CONFERENCE_ROLE_ID, CONGRESS_ID, true),
                () -> verify(proposalRepository).save(proposalEntity),
                () -> assertEquals(ROOM_ID, activityCaptor.getValue().getRoom().getId()),
                () -> assertEquals(DESCRIPTION, activityCaptor.getValue().getDescription()),
                () -> assertEquals(ActivityType.CONFERENCE, activityCaptor.getValue().getType()),
                () -> assertEquals(CONGRESS_ID, activityCaptor.getValue().getCongress().getId()),
                () -> assertEquals(USER_ID, speakerCaptor.getValue().getUser().getId()),
                () -> assertEquals(ACTIVITY_ID, speakerCaptor.getValue().getActivity().getId()),
                () -> assertTrue(proposalEntity.isUsed()),
                () -> assertEquals(ACTIVITY_ID, result.getId()),
                () -> assertEquals(ACTIVITY_NAME, result.getName())
        );
    }

    @Test
    void testCreateActivityWhenProposalIsNotApproved() throws Exception {
        // Arrange
        proposalEntity.setState(ProposalState.PENDING);

        NewActivityRequest request = new NewActivityRequest(
                ACTIVITY_NAME, ROOM_ID, PROPOSAL_ID, ACTIVITY_START, ACTIVITY_END, 100
        );

        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.of(proposalEntity));
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);

        // Assert
        assertThrows(IllegalStateException.class,
                () -> activityService.createActivity(request));
    }

    @Test
    void testCreateActivityWhenRoomOverlapExists() throws Exception {
        // Arrange
        NewActivityRequest request = new NewActivityRequest(
                ACTIVITY_NAME, ROOM_ID, PROPOSAL_ID, ACTIVITY_START, ACTIVITY_END, 100
        );

        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.of(proposalEntity));
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(activityRepository.existsOverlap(ROOM_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> activityService.createActivity(request));
    }

    @Test
    void testCreateActivityWhenProposalAlreadyUsed() throws Exception {
        // Arrange
        proposalEntity.setUsed(true);

        NewActivityRequest request = new NewActivityRequest(
                ACTIVITY_NAME, ROOM_ID, PROPOSAL_ID, ACTIVITY_START, ACTIVITY_END, 100
        );

        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.of(proposalEntity));
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(activityRepository.existsOverlap(ROOM_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(false);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> activityService.createActivity(request));
    }

    @Test
    void testCreateActivityWhenDateRangeIsInvalid() throws Exception {
        // Arrange
        NewActivityRequest request = new NewActivityRequest(
                ACTIVITY_NAME,
                ROOM_ID,
                PROPOSAL_ID,
                ACTIVITY_END,
                ACTIVITY_START,
                100
        );

        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.of(proposalEntity));
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(activityRepository.existsOverlap(ROOM_ID, ACTIVITY_END, ACTIVITY_START)).thenReturn(false);

        // Assert
        assertThrows(InvalidDateRangeException.class,
                () -> activityService.createActivity(request));
    }

    @Test
    void testCreateActivityWhenOutsideCongressDates() throws Exception {
        // Arrange
        NewActivityRequest request = new NewActivityRequest(
                ACTIVITY_NAME,
                ROOM_ID,
                PROPOSAL_ID,
                CONGRESS_START.minusHours(1),
                ACTIVITY_END,
                100
        );

        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.of(proposalEntity));
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(activityRepository.existsOverlap(ROOM_ID, CONGRESS_START.minusHours(1), ACTIVITY_END)).thenReturn(false);

        // Assert
        assertThrows(IllegalArgumentException.class,
                () -> activityService.createActivity(request));
    }

    @Test
    void testCreateActivityWithGuest() throws Exception {
        // Arrange
        Long[] users = new Long[] { USER_ID };

        NewActivityGuest request = new NewActivityGuest(
                ACTIVITY_NAME,
                ROOM_ID,
                ACTIVITY_START,
                ACTIVITY_END,
                100,
                users,
                CONGRESS_ID,
                DESCRIPTION,
                ActivityType.WORKSHOP
        );

        ArgumentCaptor<SpeakerEntity> speakerCaptor = ArgumentCaptor.forClass(SpeakerEntity.class);

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(activityRepository.existsOverlap(ROOM_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(false);
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(speakerRepository.existsSpeakerConflict(USER_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(false);
        when(activityMapper.toEntity(request)).thenReturn(activityEntity);
        when(speakerRepository.existsByUserIdAndActivityId(USER_ID, ACTIVITY_ID)).thenReturn(false);
        when(activityMapper.toActivityResponse(activityEntity)).thenReturn(activityResponse);

        // Act
        ActivityResponse result = activityService.createActivityWithGuest(request);

        // Assert
        assertAll(
                () -> verify(activityRepository).save(activityEntity),
                () -> verify(speakerRepository).save(speakerCaptor.capture()),
                () -> verify(inscriptionService).enroll(USER_ID, GUEST_ROLE_ID, CONGRESS_ID, true),
                () -> assertEquals(USER_ID, speakerCaptor.getValue().getUser().getId()),
                () -> assertEquals(ACTIVITY_ID, result.getId())
        );
    }

    @Test
    void testCreateActivityWithGuestWhenSpeakerHasConflict() throws Exception {
        // Arrange
        Long[] users = new Long[] { USER_ID };

        NewActivityGuest request = new NewActivityGuest(
                ACTIVITY_NAME,
                ROOM_ID,
                ACTIVITY_START,
                ACTIVITY_END,
                100,
                users,
                CONGRESS_ID,
                DESCRIPTION,
                ActivityType.WORKSHOP
        );

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(activityRepository.existsOverlap(ROOM_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(false);
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(speakerRepository.existsSpeakerConflict(USER_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> activityService.createActivityWithGuest(request));
    }

    @Test
    void testGetActivitiesByCongressId() throws Exception {
        // Arrange
        List<ActivityEntity> activities = List.of(activityEntity);
        List<ActivityResponse> responses = List.of(activityResponse);

        when(activityRepository.findByCongressId(CONGRESS_ID)).thenReturn(activities);
        when(activityMapper.toActivityResponseList(activities)).thenReturn(responses);

        // Act
        List<ActivityResponse> result = activityService.getActivitiesByCongressId(CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(ACTIVITY_ID, result.get(0).getId())
        );
    }

    @Test
    void testGetActivitiesByTypeAndCongressId() throws Exception {
        // Arrange
        List<ActivityEntity> activities = List.of(activityEntity);
        List<ActivityResponse> responses = List.of(activityResponse);

        when(activityRepository.findByTypeAndCongressId(ActivityType.CONFERENCE, CONGRESS_ID)).thenReturn(activities);
        when(activityMapper.toActivityResponseList(activities)).thenReturn(responses);

        // Act
        List<ActivityResponse> result = activityService.getActivitiesByTypeAndCongressId(ActivityType.CONFERENCE, CONGRESS_ID);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals(ACTIVITY_ID, result.get(0).getId())
        );
    }

    @Test
    void testCreateProposal() throws Exception {
        // Arrange
        NewProposalRequest request = new NewProposalRequest(
                CONGRESS_ID,
                USER_ID,
                PROPOSAL_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE
        );

        ArgumentCaptor<ProposalEntity> proposalCaptor = ArgumentCaptor.forClass(ProposalEntity.class);

        ProposalEntity newProposal = new ProposalEntity();
        newProposal.setName(PROPOSAL_NAME);
        newProposal.setDescription(DESCRIPTION);
        newProposal.setType(ActivityType.CONFERENCE);

        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(true);
        when(proposalRepository.existsByUserIdAndCongressIdAndState(USER_ID, CONGRESS_ID, ProposalState.PENDING))
                .thenReturn(false);
        when(activityMapper.toProposalEntity(request)).thenReturn(newProposal);
        when(activityMapper.toProposalResponse(newProposal)).thenReturn(proposalResponse);

        // Act
        ProposalResponse result = activityService.createProposal(request);

        // Assert
        assertAll(
                () -> verify(proposalRepository).save(proposalCaptor.capture()),
                () -> assertEquals(USER_ID, proposalCaptor.getValue().getUser().getId()),
                () -> assertEquals(CONGRESS_ID, proposalCaptor.getValue().getCongress().getId()),
                () -> assertEquals(PROPOSAL_ID, result.getId()),
                () -> assertEquals(PROPOSAL_NAME, result.getName())
        );
    }

    @Test
    void testCreateProposalWhenUserIsNotEnrolled() throws Exception {
        // Arrange
        NewProposalRequest request = new NewProposalRequest(
                CONGRESS_ID,
                USER_ID,
                PROPOSAL_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE
        );

        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(false);

        // Assert
        assertThrows(NotFoundException.class,
                () -> activityService.createProposal(request));
    }

    @Test
    void testCreateProposalWhenPendingProposalAlreadyExists() throws Exception {
        // Arrange
        NewProposalRequest request = new NewProposalRequest(
                CONGRESS_ID,
                USER_ID,
                PROPOSAL_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE
        );

        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(inscriptionService.isUserEnrolledInCongress(USER_ID, CONGRESS_ID)).thenReturn(true);
        when(proposalRepository.existsByUserIdAndCongressIdAndState(USER_ID, CONGRESS_ID, ProposalState.PENDING))
                .thenReturn(true);

        // Assert
        assertThrows(DuplicatedEntityException.class,
                () -> activityService.createProposal(request));
    }

    @Test
    void testGetProposalsByCongressId() throws Exception {
        // Arrange
        List<ProposalEntity> proposals = List.of(proposalEntity);
        List<ProposalResponse> responses = List.of(proposalResponse);

        when(proposalRepository.findByCongressId(CONGRESS_ID)).thenReturn(proposals);
        when(activityMapper.toProposalResponseList(proposals)).thenReturn(responses);

        // Act
        List<ProposalResponse> result = activityService.getProposalsByCongressId(CONGRESS_ID);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetProposalsByStateAndCongressId() throws Exception {
        // Arrange
        ProposalEntity pendingProposal = new ProposalEntity();
        pendingProposal.setState(ProposalState.PENDING);

        List<ProposalEntity> proposals = List.of(proposalEntity, pendingProposal);
        List<ProposalEntity> filtered = List.of(proposalEntity);
        List<ProposalResponse> responses = List.of(proposalResponse);

        when(proposalRepository.findByCongressId(CONGRESS_ID)).thenReturn(proposals);
        when(activityMapper.toProposalResponseList(filtered)).thenReturn(responses);

        // Act
        List<ProposalResponse> result = activityService.getProposalsByStateAndCongressId(ProposalState.APPROVED, CONGRESS_ID);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetProposalByUserId() throws Exception {
        // Arrange
        List<ProposalEntity> proposals = List.of(proposalEntity);
        List<ProposalResponse> responses = List.of(proposalResponse);

        when(proposalRepository.findByUserId(USER_ID)).thenReturn(proposals);
        when(activityMapper.toProposalResponseList(proposals)).thenReturn(responses);

        // Act
        List<ProposalResponse> result = activityService.getProposalByUserId(USER_ID);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void testGetProposalById() throws Exception {
        // Arrange
        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.of(proposalEntity));

        // Act
        ProposalEntity result = activityService.getProposalById(PROPOSAL_ID);

        // Assert
        assertEquals(PROPOSAL_ID, result.getId());
    }

    @Test
    void testGetProposalByIdWhenNotFound() {
        // Arrange
        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> activityService.getProposalById(PROPOSAL_ID));
    }

    @Test
    void testGetActivityById() throws Exception {
        // Arrange
        when(activityRepository.findById(ACTIVITY_ID)).thenReturn(Optional.of(activityEntity));

        // Act
        ActivityEntity result = activityService.getActivityById(ACTIVITY_ID);

        // Assert
        assertEquals(ACTIVITY_ID, result.getId());
    }

    @Test
    void testGetActivityByIdWhenNotFound() {
        // Arrange
        when(activityRepository.findById(ACTIVITY_ID)).thenReturn(Optional.empty());

        // Assert
        assertThrows(NotFoundException.class,
                () -> activityService.getActivityById(ACTIVITY_ID));
    }

    @Test
    void testUpdateProposalApproved() throws Exception {
        // Arrange
        UpdateProposal updateProposal = mock(UpdateProposal.class);
        when(updateProposal.getState()).thenReturn(ProposalState.APPROVED);

        proposalEntity.setState(ProposalState.PENDING);
        proposalEntity.setType(ActivityType.CONFERENCE);

        ProposalResponse approvedResponse = new ProposalResponse(
                PROPOSAL_ID,
                PROPOSAL_NAME,
                CONGRESS_NAME,
                USER_NAME,
                DESCRIPTION,
                ActivityType.CONFERENCE,
                ProposalState.APPROVED
        );

        when(proposalRepository.findById(PROPOSAL_ID)).thenReturn(Optional.of(proposalEntity));
        when(activityMapper.toProposalResponse(proposalEntity)).thenReturn(approvedResponse);

        // Act
        ProposalResponse result = activityService.updateProposal(PROPOSAL_ID, updateProposal);

        // Assert
        assertAll(
                () -> verify(inscriptionService).enroll(USER_ID, CONFERENCE_ROLE_ID, CONGRESS_ID, true),
                () -> verify(proposalRepository).save(proposalEntity),
                () -> assertEquals(ProposalState.APPROVED, proposalEntity.getState()),
                () -> assertEquals(ProposalState.APPROVED, result.getState())
        );
    }

    @Test
    void testGetProposalResponseById() throws Exception {
        // Arrange
        ActivityServiceImpl spy = spy(activityService);
        doReturn(proposalEntity).when(spy).getProposalById(PROPOSAL_ID);
        when(activityMapper.toProposalResponse(proposalEntity)).thenReturn(proposalResponse);

        // Act
        ProposalResponse result = spy.getProposalResponseById(PROPOSAL_ID);

        // Assert
        assertEquals(PROPOSAL_ID, result.getId());
    }

    @Test
    void testDeleteActivity() throws Exception {
        // Arrange
        ActivityServiceImpl spy = spy(activityService);
        doReturn(activityEntity).when(spy).getActivityById(ACTIVITY_ID);
        when(attendanceRepository.existsByActivityId(ACTIVITY_ID)).thenReturn(false);

        // Act
        spy.deleteAcivity(ACTIVITY_ID);

        // Assert
        verify(activityRepository).delete(activityEntity);
    }

    @Test
    void testDeleteActivityWhenHasAttendances() throws Exception {
        // Arrange
        ActivityServiceImpl spy = spy(activityService);
        doReturn(activityEntity).when(spy).getActivityById(ACTIVITY_ID);
        when(attendanceRepository.existsByActivityId(ACTIVITY_ID)).thenReturn(true);

        // Assert
        assertThrows(ActivityHasAttendancesException.class,
                () -> spy.deleteAcivity(ACTIVITY_ID));
    }

    @Test
    void testUpdateActivityThrowsClassCastExceptionDueToInvalidCast() throws Exception {
        // Arrange
        UpdateActivity request = new UpdateActivity(
                UPDATED_START,
                UPDATED_END,
                ROOM_ID,
                100L,
                UPDATED_ACTIVITY_NAME,
                DESCRIPTION
        );

        SpeakerEntity existingSpeaker = new SpeakerEntity();
        existingSpeaker.setActivity(activityEntity);
        existingSpeaker.setUser(userEntity);

        ActivityServiceImpl spy = spy(activityService);
        doReturn(activityEntity).when(spy).getActivityById(ACTIVITY_ID);

        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(speakerRepository.findAllByActivityId(ACTIVITY_ID)).thenReturn(List.of(existingSpeaker));
        when(activityRepository.existsOverlapExcludingId(ROOM_ID, UPDATED_START, UPDATED_END, ACTIVITY_ID)).thenReturn(false);

        // Assert
        assertThrows(ClassCastException.class,
                () -> spy.updateActivity(ACTIVITY_ID, request));
    }

    @Test
    void testAddSpeakerIsCalledOncePerGuestUser() throws Exception {
        // Arrange
        Long secondUserId = 6L;
        UserEntity secondUser = new UserEntity();
        secondUser.setId(secondUserId);
        secondUser.setName("Maria");

        Long[] users = new Long[] { USER_ID, secondUserId };

        NewActivityGuest request = new NewActivityGuest(
                ACTIVITY_NAME,
                ROOM_ID,
                ACTIVITY_START,
                ACTIVITY_END,
                100,
                users,
                CONGRESS_ID,
                DESCRIPTION,
                ActivityType.WORKSHOP
        );

        when(userService.getById(USER_ID)).thenReturn(userEntity);
        when(userService.getById(secondUserId)).thenReturn(secondUser);
        when(locationService.getRoomById(ROOM_ID)).thenReturn(roomEntity);
        when(activityRepository.existsOverlap(ROOM_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(false);
        when(congressService.getById(CONGRESS_ID)).thenReturn(congressEntity);
        when(speakerRepository.existsSpeakerConflict(USER_ID, ACTIVITY_START, ACTIVITY_END)).thenReturn(false);
        when(speakerRepository.existsSpeakerConflict(secondUserId, ACTIVITY_START, ACTIVITY_END)).thenReturn(false);
        when(activityMapper.toEntity(request)).thenReturn(activityEntity);
        when(speakerRepository.existsByUserIdAndActivityId(USER_ID, ACTIVITY_ID)).thenReturn(false);
        when(speakerRepository.existsByUserIdAndActivityId(secondUserId, ACTIVITY_ID)).thenReturn(false);
        when(activityMapper.toActivityResponse(activityEntity)).thenReturn(activityResponse);

        // Act
        activityService.createActivityWithGuest(request);

        // Assert
        verify(speakerRepository, times(2)).save(any(SpeakerEntity.class));
        verify(inscriptionService).enroll(USER_ID, GUEST_ROLE_ID, CONGRESS_ID, true);
        verify(inscriptionService).enroll(secondUserId, GUEST_ROLE_ID, CONGRESS_ID, true);
    }
}